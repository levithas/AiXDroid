package de.levithas.aixdroid.domain.usecase.datamanager

import android.content.Context
import android.net.Uri
import de.levithas.aixdroid.domain.model.DataPoint
import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.domain.model.DataSet
import de.levithas.aixdroid.domain.repository.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStreamReader
import java.util.Date
import javax.inject.Inject

interface ImportDataUseCase {
    suspend operator fun invoke(context: Context, uri: Uri)
}

class ImportDataUseCaseImpl @Inject constructor(
    private val dataRepository: DataRepository,
) : ImportDataUseCase {

    private val maxElementsPerCreation = 250
    private val separatorSign = ','

    override suspend fun invoke(context: Context, uri: Uri) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val inputStreamReader = InputStreamReader(inputStream)

            var lineCount = 0
            var columnList : List<DataSeries> = emptyList()
            val dataPointListList = mutableListOf<MutableList<DataPoint>>()

            inputStreamReader.forEachLine { line ->
                if (lineCount == 0) {
                    columnList = parseCSVHeaderToDataSeries(line, separatorSign)
                    runBlocking {
                        withContext(Dispatchers.IO) {
                            // Index = 0 is time which will be inserted into the datapoint
                            columnList.forEachIndexed { index, dataSeries ->
                                dataPointListList.add(mutableListOf())
                                // Initial creation of dataSeries for referencing dataPoints
                                columnList[index].id = dataRepository.addDataSeries(dataSeries)
                            }
                        }
                    }
                } else {
                    val dataList = parseCSVLineToDataPoints(line, ',')
                    dataList.forEachIndexed { index, element ->
                        dataPointListList[index].add(
                            DataPoint(
                                id = null,
                                value = element.value,
                                time = element.time
                            )
                        )
                    }

                    if (dataPointListList.any { it.count() >= maxElementsPerCreation } ) {
                        runBlocking {
                            withContext(Dispatchers.IO) {
                                dataPointListList.forEachIndexed { index, column ->
                                    if (column.isNotEmpty()) {
                                        columnList[index].id?.let { it1 -> dataRepository.addDataPoints(column, it1) }
                                    }
                                }

                                columnList.forEach { dataSeries ->
                                    dataSeries.count = dataSeries.id?.let { dataRepository.getDataPointCountByDataSeriesId(it) }
                                    dataSeries.startTime = dataSeries.id?.let { Date(dataRepository.getDataPointMinTimeByDataSeriesId(it)) }
                                    dataSeries.endTime = dataSeries.id?.let { Date(dataRepository.getDataPointMaxTimeByDataSeriesId(it)) }
                                    // Replace the old dataSeries with new dataSeries
                                    dataRepository.updateDataSeries(dataSeries)
                                }
                            }
                        }
                        dataPointListList.forEach { table -> table.clear() }
                    }
                }
                lineCount++
            }

            dataRepository.addDataSet(
                DataSet(
                    id = null,
                    name = "Test",
                    description = "Platzhalter Beschreibung blablablablabla",
                    origin = "CSV-Import",
                    columns = columnList
                )
            )
        } catch (e: IOException) {
            e.message?.let { error(it) }
        }
    }

    private fun parseCSVHeaderToDataSeries(line: String, separator: Char) : List<DataSeries> {
        // First Column is Time which is not a DataSeries
        return line.split(separator).drop(1).map {
            DataSeries(
                id = null,
                name = it,
                unit = "",
                count = null,
                startTime = null,
                endTime = null
            )
        }
    }

    private fun parseCSVLineToDataPoints(line: String, separator: Char) : List<DataPoint> {
        val values = line.split(separator)
        val pointList = mutableListOf<DataPoint>()
        values.forEachIndexed { index, s -> if (index > 0) { pointList.add(DataPoint(
            id = null,
            value = s.toFloat(),
            time = Date(values[0].toLong())
        )) } }
        return pointList
    }
}
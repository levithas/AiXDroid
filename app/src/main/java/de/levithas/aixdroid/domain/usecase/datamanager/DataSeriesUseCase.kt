package de.levithas.aixdroid.domain.usecase.datamanager

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import de.levithas.aixdroid.domain.model.DataPoint
import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.domain.model.DataSet
import de.levithas.aixdroid.data.repository.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Date
import javax.inject.Inject

interface DataSeriesUseCase {
    suspend fun importFromCSV(uri: Uri, onProgressUpdate: (Float) -> Unit)
    suspend fun checkExistingDataSeriesNames(uri: Uri) : Boolean
    suspend fun deleteDataSeries(dataSeriesId: Long)
}

class DataSeriesUseCaseImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataRepository: DataRepository,
    private val dataSetUseCase: DataSetUseCase
) : DataSeriesUseCase {

    private val maxElementsPerCreation = 250
    private val separatorSign = ','

    private var existingDataSeriesNameMap = emptyMap<String, DataSeries>()

    override suspend fun importFromCSV(uri: Uri, onProgressUpdate: (Float) -> Unit) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            val fileSize = context.contentResolver.openFileDescriptor(uri, "r")?.use { it.statSize }
            var byteCount = 0L
            var lineCount = 0L
            var dataSeriesList : List<DataSeries> = emptyList()
            val dataPointListList = mutableListOf<MutableList<DataPoint>>()

            withContext(Dispatchers.IO) {
                var line = bufferedReader.readLine()
                do {
                    coroutineContext.ensureActive()
                    byteCount += line.toByteArray().size

                    if (lineCount == 0L) {
                        if (existingDataSeriesNameMap.isEmpty()) {
                            existingDataSeriesNameMap = dataRepository.getAllDataSeriesNoFlow().associateBy { it.name }
                        }

                        dataSeriesList = parseCSVHeaderToDataSeries(line, separatorSign)

                        dataSeriesList.forEachIndexed { index, dataSeries ->
                            val existingDataSeries = existingDataSeriesNameMap[dataSeries.name]
                            if (existingDataSeries != null) {
                                dataSeries.id = existingDataSeries.id
                            }

                            if (dataSeries.id == null) {
                                dataSeries.id = dataRepository.addDataSeries(listOf(dataSeries))[0]
                            }
                            dataPointListList.add(mutableListOf())
                        }
                    } else {
                        val dataList = parseCSVLineToDataPoints(line, ',')
                        dataList.forEachIndexed { index, element ->
                            dataPointListList[index].add(
                                DataPoint(
                                    value = element.value,
                                    time = element.time
                                )
                            )
                        }

                        if (dataPointListList.any { it.count() >= maxElementsPerCreation } ) {
                            // Import Datapoints
                            dataPointListList.forEachIndexed { index, column ->
                                if (column.isNotEmpty()) {
                                    dataSeriesList[index].id?.let { it1 -> dataRepository.addDataPoints(column, it1) }
                                }
                            }

                            // Update Metadata for DataSeries
                            dataSeriesList.forEach { dataSeries ->
                                dataSeries.count = dataSeries.id?.let { dataRepository.getDataPointCountByDataSeriesId(it) }
                                dataSeries.startTime = dataSeries.id?.let { Date(dataRepository.getDataPointMinTimeByDataSeriesId(it)) }
                                dataSeries.endTime = dataSeries.id?.let { Date(dataRepository.getDataPointMaxTimeByDataSeriesId(it)) }
                                // Replace the old dataSeries with new dataSeries
                                dataRepository.updateDataSeries(dataSeries)
                            }
                            dataPointListList.forEach { table -> table.clear() }
                        }
                    }

                    lineCount++
                    fileSize?.let { onProgressUpdate(byteCount / it.toFloat()) }
                    line = bufferedReader.readLine()
                } while (line != null)
            }
        } catch (e: IOException) {
            e.message?.let { error(it) }
        }
        finally {
            existingDataSeriesNameMap = emptyMap()
        }
    }

    override suspend fun checkExistingDataSeriesNames(uri: Uri): Boolean {
        val inputStream = context.contentResolver.openInputStream(uri)
        val inputStreamReader = InputStreamReader(inputStream)
        val bufferedReader = BufferedReader(inputStreamReader)
        val result: Boolean

        withContext(Dispatchers.IO) {
            val line = bufferedReader.readLine()
            val dataSeriesList = parseCSVHeaderToDataSeries(line, separatorSign)
            existingDataSeriesNameMap = dataRepository.getAllDataSeriesNoFlow().associateBy { it.name }
            result = dataSeriesList.any { existingDataSeriesNameMap[it.name] != null }
        }

        return result
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
                endTime = null,
                origin = "CSV-Import",
            )
        }
    }

    private fun parseCSVLineToDataPoints(line: String, separator: Char) : List<DataPoint> {
        val values = line.split(separator)
        val pointList = mutableListOf<DataPoint>()
        values.forEachIndexed { index, s -> if (index > 0) { pointList.add(DataPoint(
            value = s.toFloat(),
            time = Date(values[0].toLong())
        )) } }
        return pointList
    }

    override suspend fun deleteDataSeries(dataSeriesId: Long) {
        dataRepository.deleteDataSeries(dataSeriesId)
    }
}
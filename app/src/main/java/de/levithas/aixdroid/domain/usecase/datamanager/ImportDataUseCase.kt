package de.levithas.aixdroid.domain.usecase.datamanager

import android.content.Context
import android.net.Uri
import de.levithas.aixdroid.domain.model.DataPoint
import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.domain.model.DataSet
import de.levithas.aixdroid.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URI
import java.util.Date
import javax.inject.Inject

interface ImportDataUseCase {
    suspend operator fun invoke(context: Context, uri: Uri)
}

class ImportDataUseCaseImpl @Inject constructor(
    private val dataRepository: DataRepository,
    private val readTextFileUseCase: ReadTextFileUseCase
) : ImportDataUseCase {
    override suspend fun invoke(context: Context, uri: Uri) {
        val text = readTextFileUseCase(context, uri)
        text?.let {
            val dataSeriesList = parseCSVtoDataSeriesList(it, ',')
            dataRepository.addDataSet(
                DataSet(
                    id = null,
                    name = "Test",
                    description = "Platzhalter Beschreibung blablablablabla",
                    origin = "CSV-Import",
                    columns = dataSeriesList
                )
            )
        }
    }

    private fun parseCSVtoDataSeriesList(text: String, seperator: Char) : List<DataSeries> {
        // Read Headers
        val headerList = text.substring(0, text.indexOfFirst { c -> c == '\n' }).split(seperator)
        val dataRowList = text.substring(text.indexOfFirst { c -> c == '\n' } + 1).split("\n")
        val dataSeriesList = mutableListOf<DataSeries>()

        headerList.forEachIndexed { columnIndex, title ->
            if (columnIndex > 0) {
                val dataList : MutableList<DataPoint> = emptyList<DataPoint>().toMutableList()
                dataRowList.forEach { row ->
                    val values = row.split(seperator)
                    if (values.size > 1 && values[columnIndex].isNotBlank()) {
                        dataList.add(DataPoint(
                            id = null,
                            value = values[columnIndex].toFloat(),
                            time = Date(values[0].toLong())
                        ))
                    }
                }
                if (dataList.size > 0) {
                    dataSeriesList.add(
                        DataSeries(
                            id = null,
                            name = title,
                            unit = "",
                            data = dataList
                        )
                    )
                }
            }
        }
        return dataSeriesList
    }
}
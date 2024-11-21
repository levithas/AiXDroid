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
            val dataSeriesList = parseCSVtoDataSeriesList(it)
            dataRepository.addDataSet(
                DataSet(
                    id = -1,
                    name = "Test",
                    description = "",
                    origin = "",
                    columns = dataSeriesList
                )
            )
        }
    }

    private fun parseCSVtoDataSeriesList(text: String) : List<DataSeries> {
        // Read Headers
        val headerList = text.substring(0, text.indexOfFirst { c -> c == '\n' }).split(";")
        val dataRowList = text.substring(text.indexOfFirst { c -> c == '\n' }).split("\n")
        val dataSeriesList = mutableListOf<DataSeries>()

        headerList.forEachIndexed { columnIndex, title ->
            if (columnIndex > 0) {
                val dataList : MutableList<DataPoint> = emptyList<DataPoint>().toMutableList()
                dataRowList.forEach { row ->
                    val values = row.split(";")
                    dataList.add(DataPoint(
                        id = -1,
                        value = values[columnIndex].toFloat(),
                        time = Date(values[0].toLong())
                    ))
                }
                dataSeriesList.add(
                    DataSeries(
                    id = -1,
                    name = title,
                    unit = "",
                    data = dataList
                    )
                )
            }
        }
        return dataSeriesList
    }
}
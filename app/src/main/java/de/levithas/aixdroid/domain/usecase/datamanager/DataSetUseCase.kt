package de.levithas.aixdroid.domain.usecase.datamanager

import android.provider.ContactsContract.Data
import androidx.compose.ui.node.InternalCoreApi
import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.domain.model.DataSet
import de.levithas.aixdroid.data.repository.DataRepository
import de.levithas.aixdroid.domain.model.TensorData
import javax.inject.Inject

interface DataSetUseCase {
    suspend fun createDataSet(dataSet: DataSet) : Long
    suspend fun getDataSetById(dataSetId: Long) : DataSet?
    suspend fun updateDataSet(dataSet: DataSet)
    suspend fun addDataSeriesToDataSet(dataSetId: Long, dataSeriesList: List<DataSeries>)
    suspend fun assignTensorDataList(dataSet: DataSet, tensorDataList: Map<Long, Long>)
    suspend fun removeDataSeriesFromDataSet(dataSet: DataSet, dataSeriesList: List<DataSeries>)
    suspend fun dissolveDataSet(dataSetId: Long)
}

class DataSetUseCaseImpl @Inject constructor(
    private val dataRepository: DataRepository,
) : DataSetUseCase {
    override suspend fun createDataSet(dataSet: DataSet) : Long {
        return dataRepository.addDataSet(dataSet)
    }

    override suspend fun getDataSetById(dataSetId: Long): DataSet? {
        return dataRepository.getDataSet(dataSetId)
    }

    override suspend fun updateDataSet(dataSet: DataSet) {
        dataRepository.updateDataSet(dataSet)
    }

    override suspend fun addDataSeriesToDataSet(dataSetId: Long, dataSeriesList: List<DataSeries>) {
        val dataSet = dataRepository.getDataSet(dataSetId)
        dataSet?.let {
            dataRepository.updateDataSet(DataSet(
                id = dataSet.id,
                name = dataSet.name,
                description = dataSet.description,
                columns = dataSet.columns + dataSeriesList.associateBy(keySelector = { it }, valueTransform = { null }),
                predictionSeries = dataSet.predictionSeries,
                aiModel = dataSet.aiModel,
                autoPredict = dataSet.autoPredict
            ))
        }
    }

    override suspend fun assignTensorDataList(dataSet: DataSet, tensorDataList: Map<Long, Long>) {
        dataSet.id?.let {
            tensorDataList.forEach { (tensorId, dataSeriesId) ->
                dataRepository.assignTensorDataToDataSeriesInDataSet(it, dataSeriesId, tensorId)
            }
        }
    }

    override suspend fun removeDataSeriesFromDataSet(dataSet: DataSet, dataSeriesList: List<DataSeries>) {
        dataRepository.updateDataSet(DataSet(
            id = dataSet.id,
            name = dataSet.name,
            description = dataSet.description,
            aiModel = dataSet.aiModel,
            columns = dataSet.columns.filter { dataSeriesList.any { ds -> it.key.id != ds.id } },
            predictionSeries = dataSet.predictionSeries,
            autoPredict = dataSet.autoPredict
        ))
    }

    override suspend fun dissolveDataSet(dataSetId: Long) {
        dataRepository.deleteDataSet(dataSetId)
    }
}
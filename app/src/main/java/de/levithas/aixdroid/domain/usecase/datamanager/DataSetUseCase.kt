package de.levithas.aixdroid.domain.usecase.datamanager

import android.util.Log
import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.domain.model.DataSet
import de.levithas.aixdroid.data.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

interface DataSetUseCase {
    suspend fun createDataSet(dataSet: DataSet) : Long
    suspend fun getDataSetById(dataSetId: Long) : Flow<DataSet>?
    suspend fun updateDataSet(dataSet: DataSet)
    suspend fun addDataSeriesToDataSet(dataSetId: Long, dataSeriesList: List<DataSeries>)
    suspend fun assignTensorDataList(dataSetId: Long, tensorDataList: Map<Long, Long>)
    suspend fun removeDataSeriesFromDataSet(dataSet: DataSet, dataSeriesList: List<DataSeries>)
    suspend fun dissolveDataSet(dataSetId: Long)
}

class DataSetUseCaseImpl @Inject constructor(
    private val dataRepository: DataRepository,
) : DataSetUseCase {
    override suspend fun createDataSet(dataSet: DataSet) : Long {
        return dataRepository.addDataSet(dataSet)
    }

    override suspend fun getDataSetById(dataSetId: Long): Flow<DataSet>? {
        return dataRepository.getDataSetById(dataSetId)
    }

    override suspend fun updateDataSet(dataSet: DataSet) {
        dataRepository.updateDataSet(dataSet)
    }

    override suspend fun addDataSeriesToDataSet(dataSetId: Long, dataSeriesList: List<DataSeries>) {
        val dataSet = dataRepository.getDataSetById(dataSetId).firstOrNull()
        dataSet?.let { ds ->
            dataRepository.updateDataSet(DataSet(
                id = ds.id,
                name = ds.name,
                description = ds.description,
                columns = ds.columns + dataSeriesList.associateBy(keySelector = { it }, valueTransform = { null }),
                predictionSeries = ds.predictionSeries,
                aiModel = ds.aiModel,
                autoPredict = ds.autoPredict
            ))
        }
    }

    override suspend fun assignTensorDataList(dataSetId: Long, tensorDataList: Map<Long, Long>) {
        tensorDataList.forEach { (tensorId, dataSeriesId) ->
            val result = dataRepository.assignTensorDataToDataSeriesInDataSet(dataSetId, dataSeriesId, tensorId)
            Log.i("DataSetUseCase", "Assigned Tensor: $result")
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
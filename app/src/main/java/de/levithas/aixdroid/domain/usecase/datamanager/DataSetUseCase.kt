package de.levithas.aixdroid.domain.usecase.datamanager

import androidx.compose.ui.node.InternalCoreApi
import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.domain.model.DataSet
import de.levithas.aixdroid.domain.repository.DataRepository
import javax.inject.Inject

interface DataSetUseCase {
    suspend fun createDataSet(dataSet: DataSet) : Long
    suspend fun getDataSetById(dataSetId: Long) : DataSet?
    suspend fun updateDataSet(dataSet: DataSet)
    suspend fun addDataSeriesToDataSet(dataSet: DataSet, dataSeriesList: List<DataSeries>)
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

    override suspend fun addDataSeriesToDataSet(dataSet: DataSet, dataSeriesList: List<DataSeries>) {
        dataRepository.updateDataSet(DataSet(
            id = dataSet.id,
            name = dataSet.name,
            description = dataSet.description,
            columns = dataSet.columns + dataSeriesList,
            aiModel = dataSet.aiModel
        ))
    }

    override suspend fun removeDataSeriesFromDataSet(dataSet: DataSet, dataSeriesList: List<DataSeries>) {
        dataRepository.updateDataSet(DataSet(
            id = dataSet.id,
            name = dataSet.name,
            description = dataSet.description,
            aiModel = dataSet.aiModel,
            columns = dataSet.columns.filter { dataSeriesList.any { ds -> it.id != ds.id } }
        ))
    }

    override suspend fun dissolveDataSet(dataSetId: Long) {
        dataRepository.deleteDataSet(dataSetId)
    }
}
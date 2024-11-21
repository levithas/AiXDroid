package de.levithas.aixdroid.domain.repository

import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.domain.model.DataSet
import kotlinx.coroutines.flow.Flow

interface DataRepository {
    suspend fun getDataSetList(): Flow<List<DataSet>>
    suspend fun getDataSeriesList(): Flow<List<DataSeries>>
    suspend fun getDataSet(id: Long) : DataSet?
    suspend fun getDataSetsByName(name: String) : Flow<List<DataSet>>
    suspend fun addDataSet(dataSet: DataSet) : Long
    suspend fun deleteDataSet(id: Long)
}
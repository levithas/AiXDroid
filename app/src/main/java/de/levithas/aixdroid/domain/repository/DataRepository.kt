package de.levithas.aixdroid.domain.repository

import androidx.paging.PagingData
import androidx.paging.PagingSource
import de.levithas.aixdroid.domain.model.DataPoint
import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.domain.model.DataSet
import kotlinx.coroutines.flow.Flow

interface DataRepository {
    suspend fun getAllDataSets(): Flow<List<DataSet>>
    suspend fun getAllDataSeries(): Flow<List<DataSeries>>
    suspend fun getAllDataSeriesNoFlow(): List<DataSeries>
    suspend fun getAllDataSeriesWithName(name: String): List<DataSeries>
    suspend fun getDataSet(id: Long) : DataSet?
    suspend fun getDataSetsByName(name: String) : Flow<List<DataSet>>
    suspend fun getDataPointsByDataSeriesId(id: Long) : Flow<PagingData<DataPoint>>
    suspend fun getDataPointCountByDataSeriesId(id: Long) : Long
    suspend fun getDataPointMaxTimeByDataSeriesId(id: Long): Long
    suspend fun getDataPointMinTimeByDataSeriesId(id: Long): Long
    suspend fun addDataSet(dataSet: DataSet) : Long
    suspend fun addDataSeries(dataSeries: List<DataSeries>) : List<Long>
    suspend fun addDataPoints(dataPointList: List<DataPoint>, dataSeriesId: Long) : List<Long>
    suspend fun updateDataSet(dataSet: DataSet)
    suspend fun updateDataSeries(dataSeries: DataSeries) : Int
    suspend fun deleteDataSeries(id: Long)
    suspend fun deleteDataSet(id: Long)
}
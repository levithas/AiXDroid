package de.levithas.aixdroid.data.repository

import android.net.Uri
import androidx.paging.PagingData
import androidx.paging.PagingSource
import de.levithas.aixdroid.domain.model.DataPoint
import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.domain.model.DataSet
import kotlinx.coroutines.flow.Flow

interface DataRepository {
    suspend fun getAllDataSets(): Flow<List<DataSet>>
    suspend fun getAllDataSeries(): Flow<List<DataSeries>>
    suspend fun getAllDataSeriesWithName(name: String): Flow<List<DataSeries>>
    suspend fun getAllDataSetsWithAutoInference(): Flow<List<DataSet>>
    suspend fun getDataSetById(id: Long) : Flow<DataSet>
    suspend fun getDataSetsByName(name: String) : Flow<List<DataSet>>
    suspend fun getDataPointsByDataSeriesId(id: Long, lastTime: Long, count: Int) : List<DataPoint>
    suspend fun getDataPointsByDataSeriesIdInDateRange(id: Long, startTime: Long, endTime: Long) : List<DataPoint>
    suspend fun getDataSeries(id: Long) : Flow<DataSeries>
    suspend fun getDataPointCountByDataSeriesId(id: Long) : Long
    suspend fun getDataPointMaxTimeByDataSeriesId(id: Long): Long?
    suspend fun getDataPointMinTimeByDataSeriesId(id: Long): Long?
    suspend fun getDataSeriesByName(name: String) : Flow<DataSeries>
    suspend fun addDataSet(dataSet: DataSet) : Long
    suspend fun addDataSeries(dataSeries: List<DataSeries>) : List<Long>
    suspend fun addDataPoints(dataPointList: List<DataPoint>, dataSeriesId: Long) : List<Long>
    suspend fun updateDataSet(dataSet: DataSet)
    suspend fun updateDataSeries(dataSeries: DataSeries) : Int
    suspend fun assignTensorDataToDataSeriesInDataSet(dataSetId: Long, dataSeriesId: Long, tensorDataId: Long) : Long
    suspend fun unassignTensorDataFromDataSeriesInDataSet(dataSetId: Long, dataSeriesId: Long)
    suspend fun assignModelDataToDataSet(dataSetId: Long, modelDataFileName: String)
    suspend fun unassignModelDataFromDataSet(dataSetId: Long)
    suspend fun deleteDataSeries(id: Long)
    suspend fun deleteDataSet(id: Long)
}
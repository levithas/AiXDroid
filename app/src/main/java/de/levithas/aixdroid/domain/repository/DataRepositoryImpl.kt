package de.levithas.aixdroid.domain.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.map
import de.levithas.aixdroid.data.dao.DataSetDao
import de.levithas.aixdroid.data.model.data.DBDataSet
import de.levithas.aixdroid.data.model.data.DBDataSetToDataSeries
import de.levithas.aixdroid.data.model.data.DBDataSetWithDataSeries
import de.levithas.aixdroid.data.model.data.DBDataSeries
import de.levithas.aixdroid.data.model.data.DBDataPoint
import de.levithas.aixdroid.domain.model.DataPoint
import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.domain.model.DataSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class DataRepositoryImpl @Inject constructor(
    private val dao: DataSetDao
) : DataRepository {
    override suspend fun getAllDataSets(): Flow<List<DataSet>> {
        return dao.getAllDataSets().map { flow -> flow.map { it.toDomainModel() } }
    }

    override suspend fun getDataSet(id: Long): DataSet? {
        return dao.getDataSetById(id)?.toDomainModel()
    }

    override suspend fun getDataSetsByName(name: String): Flow<List<DataSet>> {
        return dao.getDataSetsByName(name).map { flow -> flow.map { it.toDomainModel() }}
    }

    override suspend fun getAllDataSeries(): Flow<List<DataSeries>> {
        return dao.getAllDataSeries().map { flow -> flow.map { it.toDomainModel() } }
    }

    override suspend fun getAllDataSeriesNoFlow(): List<DataSeries> {
        return dao.getAllDataSeriesNoFlow().map { it.toDomainModel() }
    }

    override suspend fun getAllDataSeriesWithName(name: String): List<DataSeries> {
        return dao.getAllDataSeriesWithName(name).map { it.toDomainModel() }
    }

    override suspend fun addDataSet(dataSet: DataSet) : Long {
        val dataSetId = dao.insertDataSet(dataSet.toDBModel())
        if (dataSet.columns.isNotEmpty()) {
            dataSet.columns.forEach { dataSeries ->
                dataSeries.id?.let { dao.insertDataSetToDataSeries(DBDataSetToDataSeries(dataSetId = dataSetId, dataSeriesId = it)) }
                // TODO: Serie hinzufügen, wenn keine ID gegeben ist?
            }
        }
        return dataSetId
    }

    override suspend fun addDataSeries(dataSeries: List<DataSeries>): List<Long> {
        return dataSeries.map { dao.insertDataSeries(it.toDBModel()) }
    }

    override suspend fun addDataPoints(dataPointList: List<DataPoint>, dataSeriesId: Long): List<Long> {
        // TODO: Check if dataSeriesId exists
        return dataPointList.map { dataPoint ->
            dao.insertDataPoint(dataPoint.toDBModel(dataSeriesId))
        }
    }

    override suspend fun getDataPointsByDataSeriesId(id: Long) : Flow<PagingData<DataPoint>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { dao.getDataPointsByDataSeriesId(id) }
        ).flow.map { pagingData ->
            pagingData.map { dbDataPoint -> dbDataPoint.toDomainModel() }
        }
    }

    override suspend fun getDataPointCountByDataSeriesId(id: Long): Long {
        return dao.getDataPointCountByDataSeriesId(id)
    }

    override suspend fun getDataPointMaxTimeByDataSeriesId(id: Long): Long {
        return dao.getDataPointMaxTimeByDataSeriesId(id)
    }

    override suspend fun getDataPointMinTimeByDataSeriesId(id: Long): Long {
        return dao.getDataPointMinTimeByDataSeriesId(id)
    }

    override suspend fun updateDataSeries(dataSeries: DataSeries): Int {
        return dao.updateDataSeries(dataSeries.toDBModel())
    }

    override suspend fun deleteDataSeries(id: Long) {
        return dao.deleteDataSeries(id)
    }

    override suspend fun deleteDataSet(id: Long) {
        return dao.deleteDataSet(id)
    }

    private fun DataSet.toDBModel() : DBDataSet {
        val dbObject = DBDataSet(
            name = this.name,
            description = this.description,
            origin = this.origin,
        )
        this.id?.let { dbObject.id = it }
        return dbObject
    }

    private fun DataSeries.toDBModel() : DBDataSeries {
        val dbObject =  DBDataSeries(
            name = this.name,
            origin = this.origin,
            unit = this.unit,
            count = this.count?:0,
            startTime = this.startTime?.time,
            endTime = this.endTime?.time
        )
        this.id?.let { dbObject.id = it }
        return dbObject
    }

    private fun DataPoint.toDBModel(dataSeriesId: Long) : DBDataPoint {
        return DBDataPoint(
            time = this.time.time,
            value = this.value,
            dataSeriesId = dataSeriesId
        )
    }

    private suspend fun DBDataSeries.toDomainModel() : DataSeries {
        return DataSeries(
            id = this.id,
            origin = this.origin,
            name = this.name,
            unit = this.unit,
            count = dao.getDataPointCountByDataSeriesId(this.id),
            startTime = Date(dao.getDataPointMinTimeByDataSeriesId(this.id)),
            endTime = Date(dao.getDataPointMaxTimeByDataSeriesId(this.id))
        )
    }

    private fun DBDataPoint.toDomainModel() : DataPoint {
        return DataPoint(
            value = this.value,
            time = Date(this.time)
        )
    }

    private suspend fun DBDataSetWithDataSeries.toDomainModel() : DataSet {
        return DataSet(
            id = this.dataSet.id,
            description = this.dataSet.description,
            name = this.dataSet.name,
            origin = this.dataSet.origin,
            columns = this.columns.map { it.toDomainModel() }
        )
    }
}
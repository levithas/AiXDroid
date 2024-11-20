package de.levithas.aixdroid.domain.repository

import de.levithas.aixdroid.data.dao.DataSetDao
import de.levithas.aixdroid.data.model.DBDataSet
import de.levithas.aixdroid.data.model.DBDataSetToTimeSeries
import de.levithas.aixdroid.data.model.DBDataSetWithTimeSeries
import de.levithas.aixdroid.data.model.DBTimeSeries
import de.levithas.aixdroid.data.model.DBTimeSeriesDataPoint
import de.levithas.aixdroid.data.model.DBTimeSeriesToDataPoint
import de.levithas.aixdroid.domain.model.DataPoint
import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.domain.model.DataSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class DataRepositoryImpl @Inject constructor(
    private val dao: DataSetDao
) : DataRepository {
    override suspend fun getDataSetList(): Flow<List<DataSet>> {
        return dao.getAllDataSets().map { flow -> flow.map { it.toDomainModel() } }
    }

    override suspend fun getDataSet(id: Long): DataSet? {
        return dao.getDataSetById(id)?.toDomainModel()
    }

    override suspend fun getDataSetsByName(name: String): Flow<List<DataSet>> {
        return dao.getDataSetsByName(name).map { flow -> flow.map { it.toDomainModel() }}
    }

    override suspend fun addDataSet(dataSet: DataSet) : Long {
        val dbDataSetId = dao.insertDataSet(dataSet.toDBModel())
        for (column in dataSet.columns) {
            val dbDataSeriesId = dao.insertTimeSeries(column.toDBModel())
            for (data in column.data) {
                val dbDataPointId = dao.insertTimeSeriesDataPoint(data.toDBModel())
                dao.insertTimeSeriesToDataPoint(
                    DBTimeSeriesToDataPoint(timeSeriesId = dbDataSeriesId, dataPointId = dbDataPointId)
                )
            }
            dao.insertDataSetToTimeSeries(
                DBDataSetToTimeSeries(dataSetId = dbDataSetId, timeSeriesId = dbDataSeriesId)
            )
        }

        return dbDataSetId
    }

    override suspend fun deleteDataSet(id: Long) {
        return dao.deleteDataSet(id)
    }

    private fun DataSet.toDBModel() : DBDataSet {
        return DBDataSet(
            id = this.id,
            name = this.name,
            description = this.description,
            origin = this.origin,
        )
    }

    private fun DataSeries.toDBModel() : DBTimeSeries {
        return DBTimeSeries(
            id = this.id,
            name = this.name,
            startTime = this.startTime.time,
            valueUnit = this.unit
        )
    }

    private fun DataPoint.toDBModel() : DBTimeSeriesDataPoint {
        return DBTimeSeriesDataPoint(
            id = this.id,
            timeTick = this.time.time,
            value = this.value
        )
    }

    private fun DBDataSetWithTimeSeries.toDomainModel() : DataSet {
        return DataSet(
            id = this.dataSet.id,
            description = this.dataSet.description,
            name = this.dataSet.name,
            origin = this.dataSet.origin,
            columns = this.columns.map { column ->
                DataSeries(
                    id = column.timeSeries.id,
                    name = column.timeSeries.name,
                    startTime = Date(column.timeSeries.startTime),
                    unit = column.timeSeries.valueUnit,
                    data = column.data.map {
                        DataPoint(id = it.id, value = it.value, time = Date(it.timeTick))
                    }
                )
            }
        )
    }
}
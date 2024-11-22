package de.levithas.aixdroid.domain.repository

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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
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

    override suspend fun getDataSeriesList(): Flow<List<DataSeries>> {
        return dao.getAllDataSeries().map { flow -> flow.map { it.toDomainModel() } }
    }

    override suspend fun addDataSet(dataSet: DataSet) : Long {
        val dbDataSetId = dao.insertDataSet(dataSet.toDBModel())
        for (column in dataSet.columns) {
            val dbDataSeriesId = dao.insertDataSeries(column.toDBModel())
            column.data.forEach { data ->
                dao.insertDataPoint(data.toDBModel(dbDataSeriesId))
            }
            dao.insertDataSetToDataSeries(
                DBDataSetToDataSeries(dataSetId = dbDataSetId, dataSeriesId = dbDataSeriesId)
            )
        }

        return dbDataSetId
    }

    override suspend fun deleteDataSeries(id: Long) {
        return dao.deleteDataSeries(id)
    }

    override suspend fun deleteDataSet(id: Long) {
        return dao.deleteDataSet(id)
    }

    private fun DataSet.toDBModel() : DBDataSet {
        return DBDataSet(
            name = this.name,
            description = this.description,
            origin = this.origin,
        )
    }

    private fun DataSeries.toDBModel() : DBDataSeries {
        return DBDataSeries(
            name = this.name,
            unit = this.unit
        )
    }

    private fun DataPoint.toDBModel(dataSeriesId: Long) : DBDataPoint {
        return DBDataPoint(
            time = this.time.time,
            value = this.value,
            dataSeriesId = dataSeriesId
        )
    }

    private suspend fun DBDataSeries.toDomainModel() : DataSeries {
        val data = dao.getDataPointsByDataSeriesId(this.id)
            .map { flow -> flow.map { it.toDomainModel() } }
            .first()

        return DataSeries(
            id = this.id,
            name = this.name,
            unit = this.unit,
            data = data
        )
    }

    private fun DBDataPoint.toDomainModel() : DataPoint {
        return DataPoint(
            id = this.id,
            value = this.value,
            time = Date(this.time),
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
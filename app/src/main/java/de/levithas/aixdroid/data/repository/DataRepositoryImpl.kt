package de.levithas.aixdroid.data.repository

import android.util.Log
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class DataRepositoryImpl @Inject constructor(
    private val dao: DataSetDao,
    private val modelRepository: ModelRepository
) : DataRepository {

    override suspend fun getAllDataSets(): Flow<List<DataSet>> {
        return dao.getAllDataSets().map { flow -> flow.map { it.toDomainModel() } }
    }

    override suspend fun getAllDataSeries(): Flow<List<DataSeries>> {
        return dao.getAllDataSeries().map { flow -> flow.map { it.toDomainModel() } }
    }

    override suspend fun getAllDataSetsWithAutoInference(): Flow<List<DataSet>> {
        return dao.getAllDataSetsWithAutoInference().map { flow -> flow.map { it.toDomainModel() }}
    }

    override suspend fun getDataSetById(id: Long): Flow<DataSet> {
        return dao.getDataSetById(id).map { flow -> flow.toDomainModel() }
    }

    override suspend fun getDataSeries(id: Long): Flow<DataSeries> {
        return dao.getDataSeriesById(id).map{ flow -> flow.toDomainModel() }
    }

    override suspend fun getDataSeriesByName(name: String): Flow<DataSeries> {
        return dao.getDataSeriesByName(name).map {flow -> flow.toDomainModel() }
    }

    override suspend fun getDataSetsByName(name: String): Flow<List<DataSet>> {
        return dao.getDataSetsByName(name).map { flow -> flow.map { it.toDomainModel() }}
    }

    override suspend fun getAllDataSeriesWithName(name: String): Flow<List<DataSeries>> {
        return dao.getAllDataSeriesWithName(name).map { flow -> flow.map { it.toDomainModel() } }
    }

    override suspend fun addDataSet(dataSet: DataSet) : Long {
        val dataSetId = dao.insertDataSet(dataSet.toDBModel())
        if (dataSet.columns.isNotEmpty()) {
            dataSet.columns.forEach { (dataSeries, tensorData) ->
                dataSeries.id?.let { dao.insertDataSetToDataSeries(DBDataSetToDataSeries(
                    dataSetId = dataSetId,
                    dataSeriesId = it,
                    tensorDataId = tensorData?.id
                )) }
                // TODO: Serie hinzufügen, wenn keine ID gegeben ist?
            }
        }

        return dataSetId
    }

    override suspend fun assignTensorDataToDataSeriesInDataSet(dataSetId: Long, dataSeriesId: Long, tensorDataId: Long) : Long {
        return if (dao.getDataSetToDataSeries(dataSetId = dataSetId, dataSeriesId = dataSeriesId).firstOrNull() == null) {
            dao.insertDataSetToDataSeries(
                DBDataSetToDataSeries(
                    dataSetId = dataSetId,
                    dataSeriesId = dataSeriesId,
                    tensorDataId = tensorDataId)
            )
        } else {
            dao.updateDataSetToDataSeries(
                DBDataSetToDataSeries(
                    dataSetId = dataSetId,
                    dataSeriesId = dataSeriesId,
                    tensorDataId = tensorDataId)
            ).toLong()
        }
    }

    override suspend fun unassignTensorDataFromDataSeriesInDataSet(dataSetId: Long, dataSeriesId: Long) {
        dao.updateDataSetToDataSeries(DBDataSetToDataSeries(dataSetId = dataSetId, dataSeriesId = dataSeriesId, tensorDataId = null))
    }

    override suspend fun assignModelDataToDataSet(dataSetId: Long, modelDataFileName: String) {
        val dataSet = getDataSetById(dataSetId).firstOrNull()
        dataSet?.let { ds ->
            ds.aiModel = modelRepository.getModel(modelDataFileName)
            updateDataSet(ds)
        }
    }

    override suspend fun unassignModelDataFromDataSet(dataSetId: Long) {
        val dataSet = getDataSetById(dataSetId).firstOrNull()
        dataSet?.let {
            it.aiModel = null
            updateDataSet(it)
        }
    }

    override suspend fun addDataSeries(dataSeries: List<DataSeries>): List<Long> {
        return dataSeries.map { dao.insertDataSeries(it.toDBModel()) }
    }

    override suspend fun addDataPoints(dataPointList: List<DataPoint>, dataSeriesId: Long): List<Long> {
        if (dao.getDataSeriesById(dataSeriesId).firstOrNull() != null) {
            return dataPointList.map { dataPoint ->
                dao.insertDataPoint(dataPoint.toDBModel(dataSeriesId))
            }
        } else {
            Log.w("DataRepository", "DataSeries not found!")
            return emptyList()
        }
    }

    override suspend fun getDataPointsByDataSeriesId(id: Long, lastTime: Long, count: Int) : List<DataPoint> {
        return dao.getDataPointsInRange(id, lastTime, count).map { it.toDomainModel() }
    }

    override suspend fun getDataPointCountByDataSeriesId(id: Long): Long {
        return dao.getDataPointCountByDataSeriesId(id)
    }

    override suspend fun getDataPointMaxTimeByDataSeriesId(id: Long): Long? {
        return dao.getDataPointMaxTimeByDataSeriesId(id)
    }

    override suspend fun getDataPointMinTimeByDataSeriesId(id: Long): Long? {
        return dao.getDataPointMinTimeByDataSeriesId(id)
    }

    override suspend fun updateDataSeries(dataSeries: DataSeries): Int {
        return dao.updateDataSeries(dataSeries.toDBModel())
    }

    override suspend fun updateDataSet(dataSet: DataSet) {
        dao.updateDataSet(dataSet.toDBModel())
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
            predictionModelFileName = this.aiModel?.fileName,
            autoPredict = this.autoPredict,
            predictionDataSeriesId = this.predictionSeries?.id
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
        val count = dao.getDataPointCountByDataSeriesId(this.id)
        val startTime = dao.getDataPointMinTimeByDataSeriesId(this.id)?.let { Date(it) }
        val endTime = dao.getDataPointMaxTimeByDataSeriesId(this.id)?.let { Date(it) }

        return DataSeries(
            id = this.id,
            origin = this.origin,
            name = this.name,
            unit = this.unit,
            count = count,
            startTime = startTime,
            endTime = endTime
        )
    }

    private fun DBDataPoint.toDomainModel() : DataPoint {
        return DataPoint(
            value = this.value,
            time = Date(this.time)
        )
    }

    private suspend fun DBDataSetWithDataSeries.toDomainModel() : DataSet {
        val tensorDataList = this.tensors.map { it?.toDomainModel() }
        val featureDataSeriesList = this.columns.map { ds -> ds.toDomainModel() }
        val predictionSeries = this.dataSet.predictionDataSeriesId?.let { seriesId ->
            dao.getDataSeriesById(seriesId).firstOrNull()?.toDomainModel()
        }

        return DataSet(
            id = this.dataSet.id,
            description = this.dataSet.description,
            name = this.dataSet.name,
            columns = featureDataSeriesList.associateWith { dataSeries: DataSeries ->
                val idx = featureDataSeriesList.indexOf(dataSeries)
                if (tensorDataList.size > idx) {
                    tensorDataList[idx]
                } else {
                    null
                }
            },
            predictionSeries = predictionSeries,
            aiModel = this.dataSet.predictionModelFileName?.let { modelRepository.getModel(it) },
            autoPredict = this.dataSet.autoPredict
        )
    }
}
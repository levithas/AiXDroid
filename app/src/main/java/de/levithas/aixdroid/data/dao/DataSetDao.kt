package de.levithas.aixdroid.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import de.levithas.aixdroid.data.model.DBDataSet
import de.levithas.aixdroid.data.model.DBDataSetToTimeSeries
import de.levithas.aixdroid.data.model.DBDataSetWithTimeSeries
import de.levithas.aixdroid.data.model.DBTimeSeries
import de.levithas.aixdroid.data.model.DBTimeSeriesDataPoint
import de.levithas.aixdroid.data.model.DBTimeSeriesToDataPoint
import de.levithas.aixdroid.domain.model.DataSeries
import kotlinx.coroutines.flow.Flow

@Dao
interface DataSetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataSet(dataSet: DBDataSet) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimeSeries(dataSeries: DBTimeSeries) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimeSeriesDataPoint(dataPoint: DBTimeSeriesDataPoint) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimeSeriesToDataPoint(dbTimeSeriesToDataPoint: DBTimeSeriesToDataPoint) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataSetToTimeSeries(dbDataSetToTimeSeries: DBDataSetToTimeSeries) : Long


    @Transaction
    @Query("SELECT * FROM dbdataset")
    fun getAllDataSets(): Flow<List<DBDataSetWithTimeSeries>>

    @Transaction
    @Query("SELECT * FROM dbdataset WHERE id == :id")
    fun getDataSetById(id: Long) : DBDataSetWithTimeSeries?

    @Transaction
    @Query("SELECT * FROM dbdataset WHERE name == :name")
    fun getDataSetsByName(name: String) : Flow<List<DBDataSetWithTimeSeries>>

    @Transaction
    @Query("DELETE FROM dbdataset WHERE id == :id")
    fun deleteDataSet(id: Long)
}
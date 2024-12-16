package de.levithas.aixdroid.data.dao

import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import de.levithas.aixdroid.data.model.data.DBDataSet
import de.levithas.aixdroid.data.model.data.DBDataSetToDataSeries
import de.levithas.aixdroid.data.model.data.DBDataSetWithDataSeries
import de.levithas.aixdroid.data.model.data.DBDataSeries
import de.levithas.aixdroid.data.model.data.DBDataPoint
import kotlinx.coroutines.flow.Flow

@Dao
interface DataSetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataSet(dataSet: DBDataSet) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataSeries(dataSeries: DBDataSeries) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataPoint(dataPoint: DBDataPoint) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataSetToDataSeries(dbDataSetToDataSeries: DBDataSetToDataSeries) : Long

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateDataSetToDataSeries(dbDataSetToDataSeries: DBDataSetToDataSeries) : Int

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateDataSeries(dataSeries: DBDataSeries) : Int

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateDataSet(dataSet: DBDataSet) : Int

    @Transaction
    @Query("SELECT * FROM dbdataset")
    fun getAllDataSets(): Flow<List<DBDataSetWithDataSeries>>

    @Transaction
    @Query("SELECT * FROM DBDataSeries")
    fun getAllDataSeries(): Flow<List<DBDataSeries>>

    @Transaction
    @Query("SELECT * FROM DBDataSeries WHERE id == :id")
    fun getDataSeriesById(id: Long) : DBDataSeries?

    @Transaction
    @Query("SELECT * FROM DBDataSeries")
    fun getAllDataSeriesNoFlow(): List<DBDataSeries>

    @Transaction
    @Query("SELECT * FROM DBDATASERIES WHERE name == :name")
    fun getAllDataSeriesWithName(name: String): List<DBDataSeries>

    @Transaction
    @Query("SELECT * FROM dbdataset WHERE id == :id")
    fun getDataSetById(id: Long) : DBDataSetWithDataSeries?

    @Query("SELECT * FROM dbDataPoint WHERE dataSeriesId == :dataSeriesId AND time >= :minTime ORDER BY time ASC LIMIT :count")
    suspend fun getDataPointsInRange(dataSeriesId: Long, minTime: Long, count: Int): List<DBDataPoint>

    @Transaction
    @Query("SELECT MIN(time) FROM DBDataPoint WHERE dataSeriesId == :dataSeriesId")
    fun getDataPointMinTimeByDataSeriesId(dataSeriesId: Long) : Long

    @Transaction
    @Query("SELECT MAX(time) FROM DBDataPoint WHERE dataSeriesId == :dataSeriesId")
    fun getDataPointMaxTimeByDataSeriesId(dataSeriesId: Long) : Long

    @Transaction
    @Query("SELECT COUNT(*) FROM DBDataPoint WHERE dataSeriesId == :dataSeriesId")
    fun getDataPointCountByDataSeriesId(dataSeriesId: Long) : Long

    @Transaction
    @Query("SELECT * FROM dbdataset WHERE name == :name")
    fun getDataSetsByName(name: String) : Flow<List<DBDataSetWithDataSeries>>

    @Transaction
    @Query("DELETE FROM DBDataSeries WHERE id == :id")
    fun deleteDataSeries(id: Long)

    @Transaction
    @Query("DELETE FROM dbdataset WHERE id == :id")
    fun deleteDataSet(id: Long)
}
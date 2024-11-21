package de.levithas.aixdroid.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
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

    @Transaction
    @Query("SELECT * FROM dbdataset")
    fun getAllDataSets(): Flow<List<DBDataSetWithDataSeries>>

    @Transaction
    @Query("SELECT * FROM DBDataSeries")
    fun getAllDataSeries(): Flow<List<DBDataSeries>>

    @Transaction
    @Query("SELECT * FROM dbdataset WHERE id == :id")
    fun getDataSetById(id: Long) : DBDataSetWithDataSeries?

    @Transaction
    @Query("SELECT * FROM dbDataPoint WHERE dataSeriesId == :dataSeriesId")
    fun getDataPointsByDataSeriesId(dataSeriesId: Long) : Flow<List<DBDataPoint>>

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
package de.levithas.aixdroid.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import de.levithas.aixdroid.data.model.intent.DBIntentData
import kotlinx.coroutines.flow.Flow

@Dao
interface IntentDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntentData(intentData: DBIntentData) : Long

    @Transaction
    @Query("SELECT * FROM DBIntentData")
    suspend fun getAllIntentData() : Flow<List<DBIntentData>>

    @Transaction
    @Query("SELECT * FROM DBIntentData WHERE packageName == :packageName")
    suspend fun getIntentDataByPackageName(packageName: String) : DBIntentData?

    @Transaction
    @Query("SELECT * FROM DBIntentData WHERE name == :name")
    suspend fun getIntentDataByName(name: String) : DBIntentData?

    @Transaction
    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateIntentData(dbIntentData: DBIntentData) : Int

    @Transaction
    @Query("DELETE FROM dbintentdata WHERE packageName == :packageName")
    suspend fun deleteIntentData(packageName: String)
}
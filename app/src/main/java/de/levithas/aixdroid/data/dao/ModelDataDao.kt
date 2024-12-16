package de.levithas.aixdroid.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import de.levithas.aixdroid.data.model.ai.DBModelData
import de.levithas.aixdroid.data.model.ai.DBModelDataInput
import de.levithas.aixdroid.data.model.ai.DBModelDataOutput
import de.levithas.aixdroid.data.model.ai.DBModelWithTensors
import de.levithas.aixdroid.data.model.ai.DBTensorData
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModelData(modelData: DBModelData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTensorData(tensor: DBTensorData) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModelDataInput(modelDataInput: DBModelDataInput) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModelDataOutput(modelDataOutput: DBModelDataOutput) : Long

    @Transaction
    @Query("SELECT * FROM model_data WHERE fileName == :fileName")
    fun getModelByPath(fileName: String) : DBModelWithTensors?

    @Transaction
    @Query("SELECT * FROM model_data WHERE name == :name")
    fun getModelsByName(name: String) : Flow<List<DBModelWithTensors>>

    @Transaction
    @Query("SELECT * FROM model_data")
    fun getAllModels(): Flow<List<DBModelWithTensors>>

    @Transaction
    @Query("DELETE FROM model_data WHERE fileName == :fileName")
    fun deleteModel(fileName: String)
}
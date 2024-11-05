package de.levithas.aixdroid.data.dao

import android.net.Uri
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import de.levithas.aixdroid.data.model.DBModelData
import de.levithas.aixdroid.data.model.DBModelDataInput
import de.levithas.aixdroid.data.model.DBModelDataOutput
import de.levithas.aixdroid.data.model.DBTensorData
import de.levithas.aixdroid.domain.model.ModelData
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
    @Query("SELECT * FROM model_data WHERE uri == :uri")
    fun getModelByPath(uri: String) : ModelData?

    @Query("SELECT * FROM model_data WHERE name == :name")
    fun getModelsByName(name: String) : Flow<List<ModelData>>

    @Query("SELECT * FROM model_data")
    fun getAllModels(): Flow<List<ModelData>>

    @Query("DELETE FROM model_data WHERE uri == :uri")
    fun deleteModel(uri: String)
}
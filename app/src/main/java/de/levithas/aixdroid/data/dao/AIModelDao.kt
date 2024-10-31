package de.levithas.aixdroid.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import de.levithas.aixdroid.data.model.AIModelConfigurationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AIModelDao {
    @Insert
    suspend fun insertModel(model: AIModelConfigurationEntity)

    @Query("SELECT * FROM AIModelConfiguration WHERE id == :modelId")
    suspend fun getModelById(modelId: Long) : Flow<AIModelConfigurationEntity>

    @Query("SELECT * FROM AIModelConfiguration")
    suspend fun getAllModels(): Flow<Array<AIModelConfigurationEntity>>

    @Delete
    suspend fun deleteModel(model: AIModelConfigurationEntity)
}
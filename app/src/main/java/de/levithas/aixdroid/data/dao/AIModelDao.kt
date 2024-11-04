package de.levithas.aixdroid.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.levithas.aixdroid.data.model.AIModelConfigurationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AIModelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertModel(model: AIModelConfigurationEntity)

    @Query("SELECT * FROM AIModelConfiguration WHERE id == :modelId")
    fun getModelById(modelId: Long) : Flow<AIModelConfigurationEntity>

    @Query("SELECT * FROM AIModelConfiguration")
    fun getAllModels(): Flow<Array<AIModelConfigurationEntity>>

    @Delete
    fun deleteModel(model: AIModelConfigurationEntity)
}
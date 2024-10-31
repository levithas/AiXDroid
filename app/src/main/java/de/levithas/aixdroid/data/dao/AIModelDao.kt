package de.levithas.aixdroid.data.dao

import androidx.room.Dao
import androidx.room.Insert
import de.levithas.aixdroid.data.model.AIModelConfigurationEntity

@Dao
interface AIModelDao {
    @Insert
    fun insertModel(model: AIModelConfigurationEntity)
}
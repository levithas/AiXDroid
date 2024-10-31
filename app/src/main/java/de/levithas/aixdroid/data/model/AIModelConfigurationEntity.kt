package de.levithas.aixdroid.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AIModels")
data class AIModelConfigurationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val name: String,
    val path: String,
    val metadata: String
)

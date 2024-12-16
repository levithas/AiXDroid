package de.levithas.aixdroid.data.model.ai

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "model_data")
data class DBModelData(
    @PrimaryKey val fileName: String,
    val name: String,
    val description: String,
    val version: String,
    val author: String,
    val licence: String,
)

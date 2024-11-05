package de.levithas.aixdroid.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "model_data")
data class DBModelData(
    @PrimaryKey val uri: String,
    val name: String,
    val description: String,
    val version: String,
    val author: String,
    val licence: String,
)

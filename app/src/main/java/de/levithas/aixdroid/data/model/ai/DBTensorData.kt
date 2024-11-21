package de.levithas.aixdroid.data.model.ai

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tensor")
data class DBTensorData(
    @PrimaryKey(autoGenerate = true) val id : Long = 0,

    val name: String,
    val description: String,
    val type: Byte,
    val shape: String,
    val min: Float,
    val max: Float
)
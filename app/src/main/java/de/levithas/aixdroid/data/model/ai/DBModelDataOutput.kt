package de.levithas.aixdroid.data.model.ai

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["fileName", "id"],
    foreignKeys = [
        ForeignKey(
            entity = DBModelData::class,
            parentColumns = ["fileName"],
            childColumns = ["fileName"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DBTensorData::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class DBModelDataOutput(
    val fileName: String,
    val id: Long
)

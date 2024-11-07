package de.levithas.aixdroid.data.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["uri", "id"],
    foreignKeys = [
        ForeignKey(
            entity = DBModelData::class,
            parentColumns = ["uri"],
            childColumns = ["uri"],
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
data class DBModelDataInput(
    val uri: String,
    val id: Long
)


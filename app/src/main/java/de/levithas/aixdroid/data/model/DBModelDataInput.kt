package de.levithas.aixdroid.data.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["modelDataUri", "tensorId"],
    foreignKeys = [
        ForeignKey(entity = DBModelData::class, parentColumns = ["uri"], childColumns = ["modelDataUri"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = DBTensorData::class, parentColumns = ["id"], childColumns = ["tensorId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DBModelDataInput(
    val modelDataUri: String,
    val tensorId: Long
)


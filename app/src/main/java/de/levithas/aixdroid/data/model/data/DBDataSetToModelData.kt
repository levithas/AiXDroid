package de.levithas.aixdroid.data.model.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = DBDataSet::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("dataSetId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DBDataSetToModelData(
    @PrimaryKey val dataSetId: Long,
    val modelDataUri: String
)

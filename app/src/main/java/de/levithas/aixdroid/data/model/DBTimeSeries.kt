package de.levithas.aixdroid.data.model

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
data class DBTimeSeries(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    val name: String,
    val startTime: Long,
    val valueUnit: String,

    val dataSetId: Long
)

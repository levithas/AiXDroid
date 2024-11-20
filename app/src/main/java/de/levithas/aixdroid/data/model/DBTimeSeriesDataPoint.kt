package de.levithas.aixdroid.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = DBTimeSeries::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("dataSeriesId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DBTimeSeriesDataPoint(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    val timeTick: Long,
    val value: Float,

    val dataSeriesId: Long
)

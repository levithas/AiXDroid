package de.levithas.aixdroid.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = DBTimeSeries::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class DBTimeSeriesDataPoint(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    val timeTick: Long,
    val value: Float,

    val columnId: Long
)

package de.levithas.aixdroid.data.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["timeSeriesId", "dataPointId"],
    foreignKeys = [
        ForeignKey(
            entity = DBTimeSeries::class,
            parentColumns = ["id"],
            childColumns = ["timeSeriesId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DBTimeSeriesDataPoint::class,
            parentColumns = ["id"],
            childColumns = ["dataPointId"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class DBTimeSeriesToDataPoint(
    val timeSeriesId: Long,
    val dataPointId: Long
)

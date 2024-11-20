package de.levithas.aixdroid.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class DBTimeSeriesWithDataPoints(
    @Embedded val timeSeries: DBTimeSeries,

    @Relation(
        parentColumn = "timeSeriesId",
        entityColumn = "dataPointId",
        associateBy = Junction(DBTimeSeriesToDataPoint::class)
    )
    val data: List<DBTimeSeriesDataPoint> = emptyList()
)

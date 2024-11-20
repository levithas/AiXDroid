package de.levithas.aixdroid.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class DBDataSetWithTimeSeries(
    @Embedded val dataSet: DBDataSet,

    @Relation(
        parentColumn = "id",
        entityColumn = "timeSeriesId",
        associateBy = Junction(DBDataSetToTimeSeries::class)
    )
    val columns: List<DBTimeSeriesWithDataPoints> = emptyList()
)

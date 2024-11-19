package de.levithas.aixdroid.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class DBDatasetWithTimeSeries(
    @Embedded val dataset: DBDataset,

    @Relation(
        parentColumn = "datasetId",
        entityColumn = "timeSeriesId",
        associateBy = Junction(DBDatasetToTimeSeries::class)
    )
    val columns: List<DBTimeSeries> = emptyList()
)

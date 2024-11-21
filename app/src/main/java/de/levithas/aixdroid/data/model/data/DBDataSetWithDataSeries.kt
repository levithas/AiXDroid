package de.levithas.aixdroid.data.model.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class DBDataSetWithDataSeries(
    @Embedded val dataSet: DBDataSet,

    @Relation(
        associateBy = Junction(DBDataSetToDataSeries::class, parentColumn = "dataSetId", entityColumn = "dataSeriesId"),
        parentColumn = "id",
        entityColumn = "id"
    )
    val columns: List<DBDataSeries> = emptyList()
)

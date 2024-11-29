package de.levithas.aixdroid.data.model.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import de.levithas.aixdroid.data.model.ai.DBModelData
import de.levithas.aixdroid.data.model.ai.DBModelWithTensors

data class DBDataSetWithDataSeries(
    @Embedded val dataSet: DBDataSet,

    @Relation(
        associateBy = Junction(DBDataSetToDataSeries::class, parentColumn = "dataSetId", entityColumn = "dataSeriesId"),
        parentColumn = "id",
        entityColumn = "id"
    )
    val columns: List<DBDataSeries> = emptyList(),
    @Relation(
        associateBy = Junction(DBDataSetToModelData::class, parentColumn = "dataSetId", entityColumn = "modelDataUri"),
        parentColumn = "id",
        entityColumn = "uri"
    )
    val aiModel: DBModelData? = null
)

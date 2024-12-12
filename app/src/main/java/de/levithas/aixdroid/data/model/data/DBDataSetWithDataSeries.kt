package de.levithas.aixdroid.data.model.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import de.levithas.aixdroid.data.model.ai.DBModelData
import de.levithas.aixdroid.data.model.ai.DBModelWithTensors
import de.levithas.aixdroid.data.model.ai.DBTensorData
import de.levithas.aixdroid.domain.model.TensorData

data class DBDataSetWithDataSeries(
    @Embedded val dataSet: DBDataSet,

    @Relation(
        associateBy = Junction(DBDataSetToDataSeries::class, parentColumn = "dataSetId", entityColumn = "dataSeriesId"),
        parentColumn = "id",
        entityColumn = "id"
    )
    val columns: List<DBDataSeries> = emptyList(),
    @Relation(
        associateBy = Junction(DBDataSetToDataSeries::class, parentColumn = "dataSetId", entityColumn = "tensorDataId"),
        parentColumn = "id",
        entityColumn = "id"
    )
    val tensors: List<DBTensorData?> = emptyList()
)

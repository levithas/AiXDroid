package de.levithas.aixdroid.data.model.ai

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class DBModelWithTensors(
    @Embedded val modelData: DBModelData,
    @Relation(
        parentColumn = "fileName",
        entityColumn = "id",
        associateBy = Junction(DBModelDataInput::class)
    )
    val inputs: List<DBTensorData> = emptyList(),
    @Relation(
        parentColumn = "fileName",
        entityColumn = "id",
        associateBy = Junction(DBModelDataOutput::class)
    )
    val output: DBTensorData? = null
)

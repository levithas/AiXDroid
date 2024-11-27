package de.levithas.aixdroid.data.model.ai

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class DBModelWithTensors(
    @Embedded val modelData: DBModelData,
    @Relation(
        parentColumn = "uri",
        entityColumn = "id",
        associateBy = Junction(DBModelDataInput::class)
    )
    val inputs: List<DBTensorData> = emptyList(),
    @Relation(
        parentColumn = "uri",
        entityColumn = "id",
        associateBy = Junction(DBModelDataOutput::class)
    )
    val outputs: List<DBTensorData> = emptyList()
)

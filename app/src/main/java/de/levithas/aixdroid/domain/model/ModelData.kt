package de.levithas.aixdroid.domain.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import de.levithas.aixdroid.data.model.DBModelData
import de.levithas.aixdroid.data.model.DBModelDataInput
import de.levithas.aixdroid.data.model.DBModelDataOutput
import de.levithas.aixdroid.data.model.DBTensorData

data class ModelData(
    @Embedded val modelData: DBModelData = DBModelData(
        uri = "",  // Beispiel für einen leeren URI
        name = "",
        description = "",
        version = "",
        author = "",
        licence = ""
    ),
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

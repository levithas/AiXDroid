package de.levithas.aixdroid.domain.model

import android.net.Uri
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import de.levithas.aixdroid.data.model.DBModelData
import de.levithas.aixdroid.data.model.DBModelDataInput
import de.levithas.aixdroid.data.model.DBModelDataOutput
import de.levithas.aixdroid.data.model.DBTensorData

data class ModelData(
    val uri: Uri = Uri.EMPTY,
    val name: String = "",
    val description: String = "",
    val version: String = "",
    val author: String = "",
    val licence: String = "",

    val inputs: List<TensorData> = emptyList(),
    val outputs: List<TensorData> = emptyList(),
)

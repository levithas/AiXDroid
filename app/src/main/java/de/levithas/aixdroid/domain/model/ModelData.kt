package de.levithas.aixdroid.domain.model

import android.net.Uri

data class ModelData(
    val fileName: String,
    val name: String = "",
    val description: String = "",
    val version: String = "",
    val author: String = "",
    val licence: String = "",

    val inputs: List<TensorData> = emptyList(),
    val outputs: List<TensorData> = emptyList(),
)

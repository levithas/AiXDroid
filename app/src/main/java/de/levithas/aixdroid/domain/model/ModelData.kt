package de.levithas.aixdroid.domain.model

import android.net.Uri

data class ModelData(
    val fileName: String,
    val name: String = "",
    val description: String = "",
    val version: String = "",
    val author: String = "",
    val licence: String = "",

    val timePeriod: Int = 1,
    val n_steps: Int = 1,

    val inputs: List<TensorData> = emptyList(),
    val output: TensorData? = null,
)

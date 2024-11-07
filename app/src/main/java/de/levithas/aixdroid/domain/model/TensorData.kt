package de.levithas.aixdroid.domain.model

import org.tensorflow.lite.schema.TensorType

data class TensorData(
    val name: String,
    val description: String,
    val type: Byte,
    val shape: List<Int>,
    val min: Float,
    val max: Float
)

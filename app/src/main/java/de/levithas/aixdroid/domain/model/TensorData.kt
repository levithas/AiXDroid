package de.levithas.aixdroid.domain.model

data class TensorData(
    val name: String,
    val description: String,
    val type: String,
    val shape: List<Int>,
    val min: Float,
    val max: Float
)

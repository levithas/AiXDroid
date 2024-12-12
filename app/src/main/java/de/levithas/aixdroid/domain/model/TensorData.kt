package de.levithas.aixdroid.domain.model


data class TensorData(
    var id: Long?,
    var name: String,
    var description: String,
    var type: Byte,
    val shape: List<Int>,
    val min: Float,
    val max: Float
)

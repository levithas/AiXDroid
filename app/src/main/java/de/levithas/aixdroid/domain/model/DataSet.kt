package de.levithas.aixdroid.domain.model


data class DataSet(
    var id: Long?,
    var name: String,
    var description: String,
    val columns: Map<DataSeries, TensorData?>,
    var aiModel: ModelData?,
    var autoPredict: Boolean
)

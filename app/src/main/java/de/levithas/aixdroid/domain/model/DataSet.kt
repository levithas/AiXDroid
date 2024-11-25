package de.levithas.aixdroid.domain.model

data class DataSet(
    var id: Long?,
    var name: String,
    var description: String,
    val origin: String,
    val columns: List<DataSeries>
)

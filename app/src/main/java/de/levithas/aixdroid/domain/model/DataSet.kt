package de.levithas.aixdroid.domain.model

data class DataSet(
    val id: Long,
    val name: String,
    val description: String,
    val origin: String,
    val columns: List<DataSeries>
)

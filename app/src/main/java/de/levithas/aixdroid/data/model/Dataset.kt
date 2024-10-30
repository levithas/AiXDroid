package de.levithas.aixdroid.data.model

data class Dataset(
    val name: String,
    val origin: String,
    val data: List<Datapoint>
)

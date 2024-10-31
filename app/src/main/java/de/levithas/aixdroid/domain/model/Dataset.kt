package de.levithas.aixdroid.domain.model

data class Dataset(
    val name: String,
    val origin: String,
    val data: List<Datapoint>
)

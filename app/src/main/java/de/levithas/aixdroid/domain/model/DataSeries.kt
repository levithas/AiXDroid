package de.levithas.aixdroid.domain.model

import java.util.Date

data class DataSeries(
    val id: Long?,
    val name: String,
    val unit: String,
    val data: List<DataPoint>
)

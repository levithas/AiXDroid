package de.levithas.aixdroid.domain.model

import java.util.Date

data class DataPoint(
    var id: Long?,
    val value: Float,
    val time: Date,
)

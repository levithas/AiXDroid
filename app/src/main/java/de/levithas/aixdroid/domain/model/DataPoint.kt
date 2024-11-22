package de.levithas.aixdroid.domain.model

import java.util.Date

data class DataPoint(
    val id: Long?,
    val value: Float,
    val time: Date
)

package de.levithas.aixdroid.domain.model

import java.util.Date

data class Datapoint(
    val value: Float,
    val unit: String,
    val createdAt: Date
)

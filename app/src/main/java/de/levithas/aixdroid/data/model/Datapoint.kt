package de.levithas.aixdroid.data.model

import java.util.Date

data class Datapoint(
    val value: Float,
    val unit: String,
    val createdAt: Date
)

package de.levithas.aixdroid.domain.model

import kotlinx.coroutines.flow.Flow
import java.util.Date

data class DataSeries(
    var id: Long?,
    val origin: String,
    val name: String,
    val unit: String,
    var count: Long?,
    var startTime: Date?,
    var endTime: Date?
)

package de.levithas.aixdroid.domain.model

import kotlinx.coroutines.flow.Flow
import java.util.Date

data class DataSeries(
    val id: Long,
    val name: String,
    val startTime: Date,
    val unit: String,
    val data: Flow<List<DataPoint>>
)

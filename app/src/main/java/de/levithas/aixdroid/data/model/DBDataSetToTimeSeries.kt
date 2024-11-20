package de.levithas.aixdroid.data.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["dataSetId", "timeSeriesId"],
    foreignKeys = [
        ForeignKey(
            entity = DBDataSet::class,
            parentColumns = ["id"],
            childColumns = ["dataSetId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DBTimeSeries::class,
            parentColumns = ["id"],
            childColumns = ["timeSeriesId"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class DBDataSetToTimeSeries(
    val dataSetId: Long,
    val timeSeriesId: Long
)

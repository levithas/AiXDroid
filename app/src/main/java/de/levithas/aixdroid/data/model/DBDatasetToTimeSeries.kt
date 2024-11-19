package de.levithas.aixdroid.data.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["datasetId", "timeSeriesId"],
    foreignKeys = [
        ForeignKey(
            entity = DBDataset::class,
            parentColumns = ["id"],
            childColumns = ["datasetId"],
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
data class DBDatasetToTimeSeries(
    val datasetId: Long,
    val timeSeriesId: Long
)

package de.levithas.aixdroid.data.model.data

import androidx.room.Entity
import androidx.room.ForeignKey
import de.levithas.aixdroid.data.model.ai.DBTensorData

@Entity(
    primaryKeys = ["dataSetId", "dataSeriesId"],
    foreignKeys = [
        ForeignKey(
            entity = DBDataSet::class,
            parentColumns = ["id"],
            childColumns = ["dataSetId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DBDataSeries::class,
            parentColumns = ["id"],
            childColumns = ["dataSeriesId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DBTensorData::class,
            parentColumns = ["id"],
            childColumns = ["tensorDataId"],
            onDelete = ForeignKey.SET_NULL
        ),
    ]
)
data class DBDataSetToDataSeries(
    val dataSetId: Long,
    val dataSeriesId: Long,

    val tensorDataId: Long?
)

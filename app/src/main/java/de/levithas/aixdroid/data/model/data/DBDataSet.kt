package de.levithas.aixdroid.data.model.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import de.levithas.aixdroid.data.model.ai.DBModelData

@Entity(
    foreignKeys = [
        ForeignKey(
          entity = DBModelData::class,
            parentColumns = arrayOf("uri"),
            childColumns = arrayOf("predictionModelUri"),
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = DBDataSeries::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("predictionDataSeriesId"),
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class DBDataSet(
    val name: String,
    val description: String,

    val predictionModelUri: String?,
    val autoPredict: Boolean,

    val predictionDataSeriesId: Long?
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}

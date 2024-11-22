package de.levithas.aixdroid.data.model.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = DBDataSeries::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("dataSeriesId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DBDataPoint(
    val time: Long,
    val value: Float,

    val dataSeriesId: Long
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}

package de.levithas.aixdroid.data.model.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class DBDataSeries(
    val name: String,
    val unit: String,
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}

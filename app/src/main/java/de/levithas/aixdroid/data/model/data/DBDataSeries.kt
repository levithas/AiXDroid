package de.levithas.aixdroid.data.model.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class DBDataSeries(
    val name: String,
    val unit: String,
    var count: Long = 0,
    var startTime: Long?,
    var endTime: Long?
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}

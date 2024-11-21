package de.levithas.aixdroid.data.model.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class DBDataSeries(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    val name: String,
    val startTime: Long,
    val valueUnit: String,
)

package de.levithas.aixdroid.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DBTimeSeries(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    val name: String,
    val origin: String,
    val startTime: Long,
    val valueUnit: String,
)

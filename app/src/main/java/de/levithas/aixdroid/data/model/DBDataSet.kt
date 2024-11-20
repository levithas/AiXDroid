package de.levithas.aixdroid.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DBDataSet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    val name: String,
    val description: String,
    val origin: String
)

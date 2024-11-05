package de.levithas.aixdroid.data.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["uri", "id"],
)
data class DBModelDataInput(
    val uri: String,
    val id: Long
)


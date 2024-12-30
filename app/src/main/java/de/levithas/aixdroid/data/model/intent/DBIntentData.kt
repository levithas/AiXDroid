package de.levithas.aixdroid.data.model.intent

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["name"], unique = true)]
)
data class DBIntentData(
    @PrimaryKey val packageName: String,
    val name: String,

    val allowReadData: Boolean,
    val allowWriteData: Boolean,
    val allowInference: Boolean
)

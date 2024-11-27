package de.levithas.aixdroid.data.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DBDataSet(
    val name: String,
    val description: String,
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}

package de.levithas.aixdroid.data.model.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import de.levithas.aixdroid.data.model.ai.DBModelData

@Entity
data class DBDataSet(
    val name: String,
    val description: String,
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}

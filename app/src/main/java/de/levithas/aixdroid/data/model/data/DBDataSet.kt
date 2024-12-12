package de.levithas.aixdroid.data.model.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import de.levithas.aixdroid.data.model.ai.DBModelData

@Entity
data class DBDataSet(
    val name: String,
    val description: String,

    val predictionModelUri: String?,
    val autoPredict: Boolean,
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}

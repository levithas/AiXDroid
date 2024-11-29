package de.levithas.aixdroid.domain.model

import de.levithas.aixdroid.data.model.data.DBDataSet

data class DataSet(
    var id: Long?,
    var name: String,
    var description: String,
    val columns: List<DataSeries>,
    var aiModel: ModelData?
)

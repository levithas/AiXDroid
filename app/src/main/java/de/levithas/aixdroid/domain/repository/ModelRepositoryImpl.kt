package de.levithas.aixdroid.domain.repository

import android.net.Uri
import de.levithas.aixdroid.data.dao.ModelDataDao
import de.levithas.aixdroid.data.model.DBModelData
import de.levithas.aixdroid.data.model.DBModelDataInput
import de.levithas.aixdroid.data.model.DBModelDataOutput
import de.levithas.aixdroid.data.model.DBModelWithTensors
import de.levithas.aixdroid.data.model.DBTensorData
import de.levithas.aixdroid.domain.model.ModelData
import de.levithas.aixdroid.domain.model.TensorData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ModelRepositoryImpl @Inject constructor(
    private val dao: ModelDataDao
) : ModelRepository {

    override suspend fun addModel(model: ModelData) {
        val dbModel = model.toDBModel()

        dao.insertModelData(dbModel)
        for (input in model.inputs) {
            val tensorId = dao.insertTensorData(input.toDBModel())
            dao.insertModelDataInput(DBModelDataInput(
                uri = dbModel.uri,
                id = tensorId
            ))
        }
        for (output in model.outputs) {
            val tensorId = dao.insertTensorData(output.toDBModel())
            dao.insertModelDataOutput(DBModelDataOutput(
                uri = dbModel.uri,
                id = tensorId
            ))
        }
    }

    override suspend fun getModel(uri: Uri): ModelData? {
        return dao.getModelByPath(uri.toString())?.toModelData()
    }

    override suspend fun getModelsByName(name: String): Flow<List<ModelData>> {
        return dao.getModelsByName(name).map { models ->
                models.map { it.toModelData() }
        }
    }

    override suspend fun getModelList(): Flow<List<ModelData>> {
        return dao.getAllModels().map { models ->
            models.map { it.toModelData() }
        }
    }

    override suspend fun deleteModel(uri: Uri) {
        dao.deleteModel(uri.toString())
    }

    private fun DBModelWithTensors.toModelData() : ModelData {
        return ModelData(
            uri = Uri.parse(this.modelData.uri),
            name = this.modelData.name,
            description = this.modelData.description,
            version = this.modelData.version,
            author = this.modelData.author,
            licence = this.modelData.licence,

            inputs = this.inputs.map {
                it.toDomainModel()
            },
            outputs = this.outputs.map {
                it.toDomainModel()
            },
        )
    }

    private fun DBTensorData.toDomainModel() : TensorData {
        return TensorData(
            name = this.name,
            description = this.description,
            type = this.type,
            shape = this.shape
                .removeSurrounding("[","]")
                .split(",")
                .map { it.toInt() },
            min = this.min,
            max = this.max,
        )
    }

    private fun ModelData.toDBModel(): DBModelData {
        return DBModelData(
            uri = this.uri.toString(),
            name = this.name,
            description = this.description,
            version = this.version,
            author = this.author,
            licence = this.licence,
        )
    }

    private fun TensorData.toDBModel() : DBTensorData {
        return DBTensorData(
            name = this.name,
            description = this.description,
            shape = this.shape.joinToString { it.toString() },
            type = this.type,
            min = this.min,
            max = this.max
        )
    }
}
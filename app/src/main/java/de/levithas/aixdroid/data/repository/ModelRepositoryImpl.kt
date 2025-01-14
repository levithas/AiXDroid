package de.levithas.aixdroid.data.repository

import de.levithas.aixdroid.data.dao.ModelDataDao
import de.levithas.aixdroid.data.model.ai.DBModelData
import de.levithas.aixdroid.data.model.ai.DBModelDataInput
import de.levithas.aixdroid.data.model.ai.DBModelDataOutput
import de.levithas.aixdroid.data.model.ai.DBModelWithTensors
import de.levithas.aixdroid.data.model.ai.DBTensorData
import de.levithas.aixdroid.domain.model.ModelData
import de.levithas.aixdroid.domain.model.TensorData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

fun DBModelWithTensors.toDomainModel() : ModelData {
    return ModelData(
        fileName = this.modelData.fileName,
        name = this.modelData.name,
        description = this.modelData.description,
        version = this.modelData.version,
        author = this.modelData.author,
        licence = this.modelData.licence,

        timePeriod = this.modelData.timePeriod,
        n_steps = this.modelData.n_steps,

        inputs = this.inputs.map {
            it.toDomainModel()
        },
        output = this.output?.toDomainModel()
    )
}

fun DBTensorData.toDomainModel() : TensorData {
    return TensorData(
        id = this.id,
        name = this.name,
        description = this.description,
        type = this.type,
        shape = this.shape
            .removeSurrounding("[","]")
            .split(",")
            .map { it.trim().toInt() },
        min = this.min,
        max = this.max,
    )
}

fun ModelData.toDBModel(): DBModelData {
    return DBModelData(
        fileName = this.fileName,
        name = this.name,
        description = this.description,
        version = this.version,
        author = this.author,
        licence = this.licence,
        timePeriod = this.timePeriod,
        n_steps = this.n_steps
    )
}

fun TensorData.toDBModel() : DBTensorData {
    return DBTensorData(
        name = this.name,
        description = this.description,
        shape = this.shape.joinToString { it.toString() },
        type = this.type,
        min = this.min,
        max = this.max
    )
}

class ModelRepositoryImpl @Inject constructor(
    private val dao: ModelDataDao
) : ModelRepository {

    override suspend fun addModel(model: ModelData) {
        val dbModel = model.toDBModel()

        dao.insertModelData(dbModel)
        for (input in model.inputs) {
            val tensorId = dao.insertTensorData(input.toDBModel())
            dao.insertModelDataInput(
                DBModelDataInput(
                fileName = dbModel.fileName,
                id = tensorId
                )
            )
        }
        model.output?.let { output ->
            val tensorId = dao.insertTensorData(output.toDBModel())
            dao.insertModelDataOutput(
                DBModelDataOutput(
                    fileName = dbModel.fileName,
                    id = tensorId
                )
            )
        }
    }

    override suspend fun getModel(fileName: String): ModelData? {
        return dao.getModelByPath(fileName)?.toDomainModel()
    }

    override suspend fun getModelsByName(name: String): Flow<List<ModelData>> {
        return dao.getModelsByName(name).map { models ->
                models.map { it.toDomainModel() }
        }
    }

    override suspend fun getModelList(): Flow<List<ModelData>> {
        return dao.getAllModels().map { models ->
            models.map { it.toDomainModel() }
        }
    }

    override suspend fun deleteModel(fileName: String) {
        dao.deleteModel(fileName)
    }
}
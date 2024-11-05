package de.levithas.aixdroid.domain.repository

import android.net.Uri
import de.levithas.aixdroid.data.dao.ModelDataDao
import de.levithas.aixdroid.data.model.DBModelData
import de.levithas.aixdroid.data.model.DBModelDataInput
import de.levithas.aixdroid.data.model.DBModelDataOutput
import de.levithas.aixdroid.domain.model.ModelData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ModelRepositoryImpl @Inject constructor(
    private val dao: ModelDataDao
) : ModelRepository {

    override suspend fun addModel(model: ModelData) {
        dao.insertModelData(model.modelData)
        for (input in model.inputs) {
            val tensorId = dao.insertTensorData(input)
            dao.insertModelDataInput(DBModelDataInput(
                modelDataUri = model.modelData.uri,
                tensorId = tensorId
            ))
        }
        for (output in model.outputs) {
            val tensorId = dao.insertTensorData(output)
            dao.insertModelDataOutput(DBModelDataOutput(
                modelDataUri = model.modelData.uri,
                tensorId = tensorId
            ))
        }
    }

    override suspend fun getModel(uri: Uri): ModelData? {
        return dao.getModelByPath(uri.toString())
    }

    override suspend fun getModelsByName(name: String): Flow<List<ModelData>> {
        return dao.getModelsByName(name)
    }

    override suspend fun getModelList(): Flow<List<ModelData>> {
        return dao.getAllModels()
    }

    override suspend fun deleteModel(uri: Uri) {
        dao.deleteModel(uri.toString())
    }
}
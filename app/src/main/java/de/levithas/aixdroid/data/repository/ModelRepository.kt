package de.levithas.aixdroid.data.repository

import de.levithas.aixdroid.domain.model.ModelData
import kotlinx.coroutines.flow.Flow

interface ModelRepository {
    suspend fun getModelList(): Flow<List<ModelData>>
    suspend fun getModel(fileName: String): ModelData?
    suspend fun getModelsByName(name: String): Flow<List<ModelData>>
    suspend fun addModel(model: ModelData)
    suspend fun deleteModel(fileName: String)
}
package de.levithas.aixdroid.domain.repository

import de.levithas.aixdroid.domain.model.ModelConfiguration
import kotlinx.coroutines.flow.Flow

interface ModelRepository {
    suspend fun getModelList(): Flow<List<ModelConfiguration>>
    suspend fun getModel(id: Long): ModelConfiguration
    suspend fun getModelByName(name: String): ModelConfiguration
    suspend fun addModel(model: ModelConfiguration)
    suspend fun updateModel(model: ModelConfiguration)
    suspend fun deleteModel(id: Long)
}
package de.levithas.aixdroid.domain.repository

import de.levithas.aixdroid.domain.model.ModelConfiguration

interface ModelRepository {
    suspend fun getModelList(): List<ModelConfiguration>
    suspend fun getModel(id: Long): ModelConfiguration
    suspend fun getModelByName(name: String): ModelConfiguration
    suspend fun addModel(model: ModelConfiguration)
    suspend fun updateModel(model: ModelConfiguration)
    suspend fun deleteModel(id: Long)
}
package de.levithas.aixdroid.domain.repository

import de.levithas.aixdroid.data.model.ModelConfiguration

class ModelRepositoryImpl : ModelRepository {
    override suspend fun getModelList(): List<ModelConfiguration> {
        TODO("Not yet implemented")
    }

    override suspend fun getModel(id: Long): ModelConfiguration {
        TODO("Not yet implemented")
    }

    override suspend fun getModelByName(name: String): ModelConfiguration {
        TODO("Not yet implemented")
    }

    override suspend fun addModel(model: ModelConfiguration) {
        TODO("Not yet implemented")
    }

    override suspend fun updateModel(model: ModelConfiguration) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteModel(id: Long) {
        TODO("Not yet implemented")
    }

}
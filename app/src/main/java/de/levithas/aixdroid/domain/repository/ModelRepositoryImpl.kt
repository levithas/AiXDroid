package de.levithas.aixdroid.domain.repository

import de.levithas.aixdroid.data.dao.AIModelDao
import de.levithas.aixdroid.data.model.AIModelConfigurationEntity
import de.levithas.aixdroid.domain.model.ModelConfiguration
import javax.inject.Inject
import kotlin.io.path.pathString

class ModelRepositoryImpl @Inject constructor(
    private val dao: AIModelDao
) : ModelRepository {
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
        dao.insertModel(AIModelConfigurationEntity(
            name = model.name,
            path = model.path.pathString,
            metadata = model.meta.toString(),
        ))
    }

    override suspend fun updateModel(model: ModelConfiguration) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteModel(id: Long) {
        TODO("Not yet implemented")
    }

}
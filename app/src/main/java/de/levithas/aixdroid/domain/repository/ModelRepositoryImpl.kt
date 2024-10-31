package de.levithas.aixdroid.domain.repository

import de.levithas.aixdroid.data.dao.AIModelDao
import de.levithas.aixdroid.data.model.AIModelConfigurationEntity
import de.levithas.aixdroid.domain.model.ModelConfiguration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.tensorflow.lite.schema.Metadata
import javax.inject.Inject
import kotlin.io.path.Path
import kotlin.io.path.pathString

class ModelRepositoryImpl @Inject constructor(
    private val dao: AIModelDao
) : ModelRepository {
    override suspend fun getModelList(): Flow<List<ModelConfiguration>> {
        return dao.getAllModels().map { entities -> entities.map {item -> item.toDomainModel()} }
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
        val model = dao.getModelById(id).first()
        dao.deleteModel(model)
    }

    private fun AIModelConfigurationEntity.toDomainModel(): ModelConfiguration {
        return ModelConfiguration(
            id = this.id,
            name = this.name,
            path = Path(this.path),
            meta = Metadata() // TODO: Implement the right Metadata content
        )
    }
}
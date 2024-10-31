package de.levithas.aixdroid.domain.usecase

import de.levithas.aixdroid.domain.model.ModelConfiguration
import de.levithas.aixdroid.domain.repository.ModelRepository
import org.tensorflow.lite.schema.Metadata
import java.nio.file.Path

class AddNewAIModelUseCase(
    private val modelRepository: ModelRepository
) {
    suspend operator fun invoke(name: String, path: Path, meta: Metadata) {
        val config = ModelConfiguration(
            name = name,
            path = path,
            meta = meta
        )
        modelRepository.addModel(config)
    }
}
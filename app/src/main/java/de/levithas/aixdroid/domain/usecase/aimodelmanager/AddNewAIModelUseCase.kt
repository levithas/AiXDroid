package de.levithas.aixdroid.domain.usecase.aimodelmanager

import de.levithas.aixdroid.domain.model.ModelConfiguration
import de.levithas.aixdroid.domain.repository.ModelRepository
import org.tensorflow.lite.schema.Metadata
import java.nio.file.Path
import javax.inject.Inject

interface AddNewAIModelUseCase {
    suspend operator fun invoke(name: String, path: Path, meta: Metadata)
}


class AddNewAIModelUseCaseImpl @Inject constructor(
    private val modelRepository: ModelRepository
) : AddNewAIModelUseCase {
    override suspend operator fun invoke(name: String, path: Path, meta: Metadata) {
        val config = ModelConfiguration(
            id = -1,
            name = name,
            path = path,
            meta = meta
        )
        modelRepository.addModel(config)
    }
}

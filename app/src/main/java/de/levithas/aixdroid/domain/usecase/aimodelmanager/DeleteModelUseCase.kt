package de.levithas.aixdroid.domain.usecase.aimodelmanager

import de.levithas.aixdroid.domain.repository.ModelRepository
import javax.inject.Inject


interface DeleteModelUseCase {
    suspend operator fun invoke(modelId: Long)
}

class DeleteModelUseCaseImpl @Inject constructor(
    private val repository: ModelRepository
): DeleteModelUseCase {
    override suspend operator fun invoke(modelId: Long) {
        repository.deleteModel(modelId)
    }
}
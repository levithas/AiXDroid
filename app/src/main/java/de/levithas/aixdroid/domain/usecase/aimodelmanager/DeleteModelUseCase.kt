package de.levithas.aixdroid.domain.usecase.aimodelmanager

import de.levithas.aixdroid.domain.repository.ModelRepository
import javax.inject.Inject

class DeleteModelUseCase @Inject constructor(
    private val repository: ModelRepository
) {
    suspend operator fun invoke(modelId: Long) {
        repository.deleteModel(modelId)
    }
}
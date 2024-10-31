package de.levithas.aixdroid.domain.usecase.aimodelmanager

import de.levithas.aixdroid.domain.model.ModelConfiguration
import de.levithas.aixdroid.domain.repository.ModelRepository
import javax.inject.Inject

class GetModelByIdUseCase @Inject constructor(
    private val repository: ModelRepository
) {
    suspend operator fun invoke(modelId: Long) : ModelConfiguration {
        return repository.getModel(modelId)
    }
}
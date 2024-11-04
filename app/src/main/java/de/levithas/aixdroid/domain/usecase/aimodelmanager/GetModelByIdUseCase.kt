package de.levithas.aixdroid.domain.usecase.aimodelmanager

import de.levithas.aixdroid.domain.model.ModelConfiguration
import de.levithas.aixdroid.domain.repository.ModelRepository
import javax.inject.Inject

interface GetModelByIdUseCase {
    suspend operator fun invoke(modelId: Long) : ModelConfiguration
}

class GetModelByIdUseCaseImpl @Inject constructor(
    private val repository: ModelRepository
): GetModelByIdUseCase {
    override suspend operator fun invoke(modelId: Long) : ModelConfiguration {
        return repository.getModel(modelId)
    }
}
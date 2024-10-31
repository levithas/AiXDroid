package de.levithas.aixdroid.domain.usecase.aimodelmanager

import de.levithas.aixdroid.domain.model.ModelConfiguration
import de.levithas.aixdroid.domain.repository.ModelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetModelListUseCase @Inject constructor(
    private val modelRepository: ModelRepository
) {
    suspend operator fun invoke() : Flow<List<ModelConfiguration>> {
        return modelRepository.getModelList()
    }
}
package de.levithas.aixdroid.domain.usecase.aimodelmanager

import de.levithas.aixdroid.domain.model.ModelData
import de.levithas.aixdroid.domain.repository.ModelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


interface GetModelListUseCase {
    suspend operator fun invoke(): Flow<List<ModelData>>
}

class GetModelListUseCaseImpl @Inject constructor(
    private val modelRepository: ModelRepository
): GetModelListUseCase {
    override suspend operator fun invoke() : Flow<List<ModelData>> {
        return modelRepository.getModelList()
    }
}
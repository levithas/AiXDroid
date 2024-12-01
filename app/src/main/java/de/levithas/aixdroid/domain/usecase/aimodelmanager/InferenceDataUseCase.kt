package de.levithas.aixdroid.domain.usecase.aimodelmanager

import javax.inject.Inject

interface InferenceDataUseCase {
    suspend fun startInference()
}

class InferenceDataUseCaseImpl @Inject constructor(

) : InferenceDataUseCase {
    override suspend fun startInference() {
        TODO("Not yet implemented")
    }
}
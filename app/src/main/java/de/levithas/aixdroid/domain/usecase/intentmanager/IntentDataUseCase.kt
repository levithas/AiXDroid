package de.levithas.aixdroid.domain.usecase.intentmanager

import de.levithas.aixdroid.data.repository.ExternalIntentRepository
import de.levithas.aixdroid.domain.model.ExternalIntentConfiguration
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface IntentDataUseCase {
    suspend fun createUpdateExternalIntentConfiguration(externalIntentConfiguration: ExternalIntentConfiguration)
    suspend fun getAllExternalIntentConfigurations() : Flow<List<ExternalIntentConfiguration>>
    suspend fun getExternalIntentConfiguration(packageName: String) : ExternalIntentConfiguration?
    suspend fun deleteExternalIntentConfiguration(packageName: String)
    suspend fun externalIntentServiceUpdate()
}

class IntentDataUseCaseImpl @Inject constructor(
    private val externalIntentRepository: ExternalIntentRepository
) : IntentDataUseCase {
    override suspend fun getAllExternalIntentConfigurations(): Flow<List<ExternalIntentConfiguration>> {
        return externalIntentRepository.getExternalIntentList()
    }

    override suspend fun createUpdateExternalIntentConfiguration(externalIntentConfiguration: ExternalIntentConfiguration) {
        externalIntentRepository.updateExternalIntent(externalIntentConfiguration)
    }

    override suspend fun getExternalIntentConfiguration(packageName: String): ExternalIntentConfiguration? {
        return externalIntentRepository.getExternalIntent(packageName)
    }

    override suspend fun deleteExternalIntentConfiguration(packageName: String) {
        externalIntentRepository.deleteExternalIntent(packageName)
    }

    override suspend fun externalIntentServiceUpdate() {
        TODO("Not yet implemented")
    }
}
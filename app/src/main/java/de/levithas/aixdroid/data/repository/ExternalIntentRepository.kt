package de.levithas.aixdroid.data.repository

import de.levithas.aixdroid.domain.model.ExternalIntentConfiguration
import kotlinx.coroutines.flow.Flow

interface ExternalIntentRepository {
    suspend fun getExternalIntentList() : Flow<List<ExternalIntentConfiguration>>
    suspend fun getExternalIntent(packageName: String) : ExternalIntentConfiguration?
    suspend fun getExternalIntentByName(name: String) : ExternalIntentConfiguration?
    suspend fun addExternalIntent(externalIntentConfiguration: ExternalIntentConfiguration)
    suspend fun updateExternalIntent(externalIntentConfiguration: ExternalIntentConfiguration)
    suspend fun deleteExternalIntent(packageName: String)
}
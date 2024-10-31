package de.levithas.aixdroid.domain.repository

import de.levithas.aixdroid.domain.model.ExternalIntentConfiguration

interface ExternalIntentRepository {
    suspend fun getExternalIntentList() : List<ExternalIntentConfiguration>
    suspend fun getExternalIntent(id: Long) : ExternalIntentConfiguration
    suspend fun getExternalIntentByName(name: String) : ExternalIntentConfiguration
    suspend fun addExternalIntent(externalIntentConfiguration: ExternalIntentConfiguration)
    suspend fun updateExternalIntent(externalIntentConfiguration: ExternalIntentConfiguration)
    suspend fun deleteExternalIntent(id: Long)
}
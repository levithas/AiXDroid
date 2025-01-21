package de.levithas.aixdroid.data.repository

import de.levithas.aixdroid.data.dao.IntentDataDao
import de.levithas.aixdroid.data.model.intent.DBIntentData
import de.levithas.aixdroid.domain.model.ExternalIntentConfiguration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class ExternalIntentRepositoryImpl @Inject constructor(
    private val dao: IntentDataDao
) : ExternalIntentRepository {
    override suspend fun getExternalIntentList(): Flow<List<ExternalIntentConfiguration>> {
        return dao.getAllIntentData().map { flow -> flow.map { it.toDomainModel() }}
    }

    override suspend fun getExternalIntent(packageName: String): ExternalIntentConfiguration? {
        return dao.getIntentDataByPackageName(packageName)?.toDomainModel()
    }

    override suspend fun getExternalIntentByName(name: String): ExternalIntentConfiguration? {
        return dao.getIntentDataByName(name)?.toDomainModel()
    }

    override suspend fun addExternalIntent(externalIntentConfiguration: ExternalIntentConfiguration) {
        dao.insertIntentData(externalIntentConfiguration.toDBModel())
    }

    override suspend fun updateExternalIntent(externalIntentConfiguration: ExternalIntentConfiguration) {
        dao.updateIntentData(externalIntentConfiguration.toDBModel())
    }

    override suspend fun deleteExternalIntent(packageName: String) {
        dao.deleteIntentData(packageName)
    }

    private fun DBIntentData.toDomainModel() : ExternalIntentConfiguration {
        return ExternalIntentConfiguration(
            name = this.name,
            packageName = this.packageName,
            allowReadData = this.allowReadData,
            allowWriteData = this.allowWriteData,
            allowInference = this.allowInference
        )
    }

    private fun ExternalIntentConfiguration.toDBModel() : DBIntentData {
        return DBIntentData(
            packageName = this.packageName,
            name = this.name,
            allowReadData = this.allowReadData,
            allowWriteData = this.allowWriteData,
            allowInference = this.allowInference
        )
    }
}
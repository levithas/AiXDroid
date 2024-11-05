package de.levithas.aixdroid.domain.repository

import android.net.Uri
import de.levithas.aixdroid.domain.model.ModelData
import kotlinx.coroutines.flow.Flow

interface ModelRepository {
    suspend fun getModelList(): Flow<List<ModelData>>
    suspend fun getModel(uri: Uri): ModelData?
    suspend fun getModelsByName(name: String): Flow<List<ModelData>>
    suspend fun addModel(model: ModelData)
    suspend fun deleteModel(uri: Uri)
}
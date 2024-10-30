package de.levithas.aixdroid.domain.repository

import de.levithas.aixdroid.data.model.Dataset

interface DataRepository {
    suspend fun getDatasetList(): List<Dataset>
    suspend fun getDataset(id: Long): Dataset
    suspend fun getDatasetByName(name: String): Dataset
    suspend fun AddDataset()
    suspend fun updateDataset(dataset: Dataset)
    suspend fun deleteDataset(id: Long)
}
package de.levithas.aixdroid.domain.repository

import de.levithas.aixdroid.data.dao.DatasetDao
import de.levithas.aixdroid.domain.model.Dataset
import javax.inject.Inject

class DataRepositoryImpl @Inject constructor(
    private val dao: DatasetDao
) : DataRepository {
    override suspend fun getDatasetList(): List<Dataset> {
        TODO("Not yet implemented")
    }

    override suspend fun getDataset(id: Long): Dataset {
        TODO("Not yet implemented")
    }

    override suspend fun getDatasetByName(name: String): Dataset {
        TODO("Not yet implemented")
    }

    override suspend fun addDataset() {
        TODO("Not yet implemented")
    }

    override suspend fun updateDataset(dataset: Dataset) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDataset(id: Long) {
        TODO("Not yet implemented")
    }

}
package de.levithas.aixdroid.domain.usecase.datamanager

import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.domain.model.DataSet
import de.levithas.aixdroid.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetDataListsUseCase {
    suspend fun getDataSetsFlow() : Flow<List<DataSet>>
    suspend fun getDataSeriesFlow() : Flow<List<DataSeries>>
}

class GetDataListsUseCaseImpl @Inject constructor(
    private val repository : DataRepository
) : GetDataListsUseCase {
    override suspend fun getDataSeriesFlow(): Flow<List<DataSeries>> {
        return repository.getAllDataSeries()
    }

    override suspend fun getDataSetsFlow(): Flow<List<DataSet>> {
        return repository.getAllDataSets()
    }
}
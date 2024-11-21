package de.levithas.aixdroid.domain.usecase.datamanager

import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetDataSeriesListUseCase {
    suspend operator fun invoke() : Flow<List<DataSeries>>
}

class GetDataSeriesListUseCaseImpl @Inject constructor(
    private val repository : DataRepository
) : GetDataSeriesListUseCase {
    override suspend fun invoke(): Flow<List<DataSeries>> {
        return repository.getDataSeriesList()
    }
}
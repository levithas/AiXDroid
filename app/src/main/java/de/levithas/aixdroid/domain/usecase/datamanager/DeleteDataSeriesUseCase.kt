package de.levithas.aixdroid.domain.usecase.datamanager

import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.domain.repository.DataRepository
import javax.inject.Inject

interface DeleteDataSeriesUseCase {
    suspend operator fun invoke(dataSeriesId: Long)
}

class DeleteDataSeriesUseCaseImpl @Inject constructor(
    private val repository: DataRepository
) : DeleteDataSeriesUseCase {
    override suspend fun invoke(dataSeriesId: Long) {
        repository.deleteDataSeries(dataSeriesId)
    }
}
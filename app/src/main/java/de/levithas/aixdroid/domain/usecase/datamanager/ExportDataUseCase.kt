package de.levithas.aixdroid.domain.usecase.datamanager

import android.content.Context
import de.levithas.aixdroid.domain.model.DataSet
import de.levithas.aixdroid.data.repository.DataRepository
import java.net.URI
import javax.inject.Inject

interface ExportDataUseCase {
    suspend operator fun invoke(context: Context, dataSet: DataSet, uri: URI)
}

class ExportDataUseCaseImpl @Inject constructor (
    private val dataRepository: DataRepository
) : ExportDataUseCase {
    override suspend fun invoke(context: Context, dataSet: DataSet, uri: URI) {
        TODO("Not yet implemented")
    }
}
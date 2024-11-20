package de.levithas.aixdroid.domain.usecase.datamanager

import android.content.Context
import de.levithas.aixdroid.domain.model.DataSet
import de.levithas.aixdroid.domain.repository.DataRepository
import java.net.URI
import javax.inject.Inject

interface ImportDataUseCase {
    suspend operator fun invoke(context: Context, uri: URI)
}

class ImportDataUseCaseImpl @Inject constructor(
    private val dataRepository: DataRepository
) : ImportDataUseCase {
    override suspend fun invoke(context: Context, uri: URI) {
        TODO("Not yet implemented")
    }
}
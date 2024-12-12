package de.levithas.aixdroid.domain.usecase.aimodelmanager

import android.net.Uri
import de.levithas.aixdroid.data.repository.ModelRepository
import java.nio.file.Path
import javax.inject.Inject
import kotlin.io.path.pathString


interface DeleteModelUseCase {
    suspend operator fun invoke(uri: Uri)
}

class DeleteModelUseCaseImpl @Inject constructor(
    private val repository: ModelRepository
): DeleteModelUseCase {
    override suspend operator fun invoke(uri: Uri) {
        repository.deleteModel(uri)
    }
}
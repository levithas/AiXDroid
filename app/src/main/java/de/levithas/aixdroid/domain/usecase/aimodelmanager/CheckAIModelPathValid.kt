package de.levithas.aixdroid.domain.usecase.aimodelmanager

import android.net.Uri
import de.levithas.aixdroid.domain.repository.ModelRepository
import java.nio.file.Path
import javax.inject.Inject
import kotlin.io.path.isRegularFile

interface CheckAIModelPathValid {
    suspend operator fun invoke(uri: Uri) : Boolean
}


class CheckAIModelPathValidImpl @Inject constructor(
    private val modelRepository: ModelRepository
) : CheckAIModelPathValid {
    override suspend fun invoke(uri: Uri): Boolean {
        return true
    }
}
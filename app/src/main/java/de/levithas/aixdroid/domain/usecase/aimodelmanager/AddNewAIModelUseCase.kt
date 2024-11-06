package de.levithas.aixdroid.domain.usecase.aimodelmanager

import android.net.Uri
import de.levithas.aixdroid.domain.model.ModelData
import de.levithas.aixdroid.domain.model.TensorData
import de.levithas.aixdroid.domain.repository.ModelRepository
import javax.inject.Inject

interface AddNewAIModelUseCase {
    suspend operator fun invoke(uri: Uri)
}

class AddNewAIModelUseCaseImpl @Inject constructor(
    private val modelRepository: ModelRepository,
) : AddNewAIModelUseCase {
    override suspend operator fun invoke(uri: Uri) {
        if(checkUriValid(uri))
        {
            val path = uri.path.toString().split("/")
            val name = path[path.size-1]

            val modelData = ModelData(
                uri = uri,
                name = name,
                description = "",
                version = "",
                author = "",
                licence = "",
                inputs = emptyList(),
                outputs = emptyList()
            )

            modelRepository.addModel(modelData)
        }
    }

    private fun checkUriValid(uri: Uri) : Boolean {
        return true
    }
}

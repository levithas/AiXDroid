package de.levithas.aixdroid.domain.usecase.aimodelmanager

import android.net.Uri
import de.levithas.aixdroid.data.model.DBModelData
import de.levithas.aixdroid.data.model.DBTensorData
import de.levithas.aixdroid.domain.model.ModelData
import de.levithas.aixdroid.domain.repository.ModelRepository
import java.nio.file.Path
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

            val dbModelData = DBModelData(
                uri = uri.path.toString(),
                name = name,
                description = "",
                version = "",
                author = "",
                licence = ""
            )
            var inputTensorList: List<DBTensorData> = emptyList()
            var outputTensorList: List<DBTensorData> = emptyList()

            val modelData = ModelData(
                modelData = dbModelData,
                inputs = inputTensorList,
                outputs = outputTensorList
            )
            modelRepository.addModel(modelData)
        }
    }

    private fun checkUriValid(uri: Uri) : Boolean {
        return true
    }
}

package de.levithas.aixdroid.domain.usecase.aimodelmanager

import android.content.Context
import android.net.Uri
import de.levithas.aixdroid.domain.model.ModelData
import de.levithas.aixdroid.domain.model.TensorData
import de.levithas.aixdroid.domain.repository.ModelRepository
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.metadata.MetadataExtractor
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.util.Objects
import javax.inject.Inject

interface AddNewAIModelUseCase {
    suspend operator fun invoke(context: Context, uri: Uri)
}

class AddNewAIModelUseCaseImpl @Inject constructor(
    private val modelRepository: ModelRepository,
) : AddNewAIModelUseCase {
    override suspend operator fun invoke(context: Context, uri: Uri) {
        try {
            var byteBuffer = getByteBufferFromUri(context, uri)
            val interpreter = Interpreter(byteBuffer)
            byteBuffer.position(0)

            val extractor = MetadataExtractor(byteBuffer)

            val inputList: MutableList<TensorData> = emptyList<TensorData>().toMutableList()
            val outputList: MutableList<TensorData> = emptyList<TensorData>().toMutableList()

            for (idx in 0..<extractor.inputTensorCount) {
                inputList.add(
                    TensorData(
                        name = extractor.getInputTensorMetadata(idx)?.name()?: "",
                        description = extractor.getInputTensorMetadata(idx)?.description()?: "",
                        type = extractor.getInputTensorType(idx),
                        shape = extractor.getInputTensorShape(idx).toList(),
                        min = 0.0f,
                        max = 0.0f,
                    )
                )
            }
            for (idx in 0..<extractor.outputTensorCount) {
                outputList.add(
                    TensorData(
                        name = extractor.getOutputTensorMetadata(idx)?.name()?: "",
                        description = extractor.getOutputTensorMetadata(idx)?.description()?: "",
                        type = extractor.getOutputTensorType(idx),
                        shape = extractor.getOutputTensorShape(idx).toList(),
                        min = 0.0f,
                        max = 0.0f,
                    )
                )
            }

            val modelData = ModelData(
                uri = uri,
                name = extractor.modelMetadata.name()?: "",
                description = extractor.modelMetadata.description()?: "",
                version = extractor.modelMetadata.version()?: "",
                author = extractor.modelMetadata.author()?: "",
                licence = extractor.modelMetadata.license()?: "",
                inputs = inputList,
                outputs = outputList
            )

            modelRepository.addModel(modelData)
        } catch (e: IOException) {
            // TODO: Log Fehler auswurf und gib es an den User weiter
            // TODO: Ergänze weitere Exceptions (IOException,...) für besseres Fehlermanagement
            throw IOException(e)
        }
    }

    @Throws(IOException::class)
    private fun getByteBufferFromUri(context: Context, uri: Uri): ByteBuffer {
        Objects.requireNonNull<InputStream>(context.contentResolver.openInputStream(uri)).use { stream ->
            val buffer = ByteArray(1024)

            val baos = ByteArrayOutputStream()
            while (stream.read(buffer) != -1) {
                baos.write(buffer)
            }

            val byteBuffer = MappedByteBuffer.allocateDirect(baos.size())
            byteBuffer.put(baos.toByteArray())
            byteBuffer.order(ByteOrder.nativeOrder())

            return byteBuffer
        }
    }
}

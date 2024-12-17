package de.levithas.aixdroid.domain.usecase.aimodelmanager

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import de.levithas.aixdroid.data.repository.ModelRepository
import de.levithas.aixdroid.domain.model.ModelData
import de.levithas.aixdroid.domain.model.TensorData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.metadata.MetadataExtractor
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.util.UUID
import javax.inject.Inject

interface AIModelUseCase {
    suspend fun addNewModel(context: Context, uri: Uri)
    suspend fun openModelFile(context: Context, fileName: String) : ByteBuffer?
    suspend fun deleteModel(context: Context, fileName: String)
}

class AIModelUseCaseImpl @Inject constructor(
    private val modelRepository: ModelRepository,
) : AIModelUseCase {
    override suspend fun addNewModel(context: Context, uri: Uri) {
        try {
            copyFileToAppInternalStorage(context, uri)?.let { fileName ->
                openModelFile(context, fileName)?.let { fileContent ->

                    val interpreter = Interpreter(fileContent) // Test call for interpreter
                    fileContent.position(0)

                    val extractor = MetadataExtractor(fileContent)

                    val inputList: MutableList<TensorData> = emptyList<TensorData>().toMutableList()
                    val outputList: MutableList<TensorData> = emptyList<TensorData>().toMutableList()

                    for (idx in 0..<extractor.inputTensorCount) {
                        inputList.add(
                            TensorData(
                                id = null,
                                name = extractor.getInputTensorMetadata(idx)?.name() ?: "",
                                description = extractor.getInputTensorMetadata(idx)?.description() ?: "",
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
                                id = null,
                                name = extractor.getOutputTensorMetadata(idx)?.name() ?: "",
                                description = extractor.getOutputTensorMetadata(idx)?.description() ?: "",
                                type = extractor.getOutputTensorType(idx),
                                shape = extractor.getOutputTensorShape(idx).toList(),
                                min = 0.0f,
                                max = 0.0f,
                            )
                        )
                    }

                    val modelData = ModelData(
                        fileName = fileName,
                        name = extractor.modelMetadata.name() ?: "",
                        description = extractor.modelMetadata.description() ?: "",
                        version = extractor.modelMetadata.version() ?: "",
                        author = extractor.modelMetadata.author() ?: "",
                        licence = extractor.modelMetadata.license() ?: "",
                        timePeriod = 0.0f,
                        inputs = inputList,
                        outputs = outputList
                    )

                    modelRepository.addModel(modelData)
                }
            }
        } catch (e: SecurityException) {
            // Handle the exception (e.g. show an error to the user)
            Log.e("URI Permission", "Failed to persist permission for URI", e)
        } catch (e: IOException) {
            Log.e("IO", "An error occured: ", e)
            // TODO: Log Fehler auswurf und gib es an den User weiter
            // TODO: Ergänze weitere Exceptions (IOException,...) für besseres Fehlermanagement
        }
    }

    private fun copyFileToAppInternalStorage(context: Context, uri: Uri) : String? {
        val inputStream = context.contentResolver.openInputStream(uri)
        if (inputStream != null) {
            val fileName = UUID.randomUUID().toString()

            val outputFile = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(outputFile)

            inputStream.copyTo(outputStream)

            inputStream.close()
            outputStream.close()

            return fileName
        }
        return null
    }

    override suspend fun openModelFile(context: Context, fileName: String) : ByteBuffer? {
        try {
            val file = File(context.filesDir, fileName)

            if (file.exists()) {
                val baos = ByteArrayOutputStream()

                withContext(Dispatchers.IO) {
                    baos.write(file.readBytes())
                }

                val byteBuffer = MappedByteBuffer.allocateDirect(baos.size())
                byteBuffer.put(baos.toByteArray())
                byteBuffer.order(ByteOrder.nativeOrder())

                return byteBuffer
            } else {
                return null
            }
        } catch (e: Exception) {
            Log.e("File Access", "Unable to open input stream for $fileName")
            e.printStackTrace()
            return null
        }
    }

    override suspend fun deleteModel(context: Context, fileName: String) {
        context.deleteFile(fileName)
        modelRepository.deleteModel(fileName)
    }
}

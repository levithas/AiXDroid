package de.levithas.aixdroid.domain.usecase.aimodelmanager

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import de.levithas.aixdroid.domain.model.DataSet
import de.levithas.aixdroid.domain.usecase.datamanager.DataSeriesUseCase
import kotlinx.coroutines.yield
import org.tensorflow.lite.Interpreter
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import javax.inject.Inject

interface InferenceDataUseCase {
    suspend fun startInference(context: Context, dataSet: DataSet)
}

class InferenceDataUseCaseImpl @Inject constructor(
    private val dataSeriesUseCase: DataSeriesUseCase
) : InferenceDataUseCase {
    override suspend fun startInference(context: Context, dataSet: DataSet) {
        dataSet.aiModel?.uri?.let {
            loadInterpreter(context, it)?.let { interpreter ->
                val inputVector = Array(1) { Array(50) { Array(2) { 0.0f } } }
                val outputVector = Array(1) { Array(50) { Array(1) { 0.0f } } }

                interpreter.run(inputVector, outputVector)

                savePredictionDataSeries(outputVector)


                interpreter.close()
            }
        }

    }

    private fun savePredictionDataSeries(outputVector: Array<Array<Array<Float>>>) {

    }

    private fun loadInterpreter(context: Context, uri: Uri) : Interpreter? {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.let {
                    val buffer = ByteArray(1024)
                    val baos = ByteArrayOutputStream()
                    while(inputStream.read(buffer) != -1) {
                        baos.write(buffer)
                    }

                    val byteBuffer: ByteBuffer = MappedByteBuffer.allocateDirect(baos.size())
                    byteBuffer.put(baos.toByteArray())
                    byteBuffer.order(ByteOrder.nativeOrder())

                    return Interpreter(byteBuffer)
                }
            }
        } catch (e: Exception) {
            e.message?.let { error(it) }
        }
        return null
    }
}
package de.levithas.aixdroid.domain.usecase.aimodelmanager

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import de.levithas.aixdroid.domain.model.DataPoint
import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.domain.model.DataSet
import de.levithas.aixdroid.domain.usecase.datamanager.DataSeriesUseCase
import org.tensorflow.lite.Interpreter
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.util.Date
import javax.inject.Inject

interface InferenceDataUseCase {
    suspend fun startInference(dataSet: DataSet, onProgressUpdate: (Float) -> Unit)
}

class InferenceDataUseCaseImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataSeriesUseCase: DataSeriesUseCase
) : InferenceDataUseCase {

    private val inferenceBufferSize = 10000
    private val predictionBufferSize = 1000

    override suspend fun startInference(dataSet: DataSet, onProgressUpdate: (Float) -> Unit) {
        dataSet.aiModel?.uri?.let {
            loadInterpreter(context, it)?.let { interpreter ->

                dataSet.predictionSeries?.let { predictionSeries ->
                    val startTime = predictionSeries.startTime?: Date(0)
                    val endTime = predictionSeries.endTime?:Date(Long.MAX_VALUE)
                    var currentStartTime = startTime
                    var continueLoop = true
                    val featureList = mutableListOf<List<DataPoint>>()
                    val predictionList = mutableListOf<DataPoint>()

                    while(continueLoop) {
                        for (series in dataSet.columns) {
                            dataSeriesUseCase.getDataPointsFromDataSeries(series.key, currentStartTime, inferenceBufferSize)?.let { dataPointList ->
                                if (dataPointList.isNotEmpty()) {
                                    featureList.add(dataPointList)
                                }
                            }
                        }

                        if (featureList.isNotEmpty()) {
                            val featureMap = transformToMap(featureList)
                            val predictedData = inferenceOnDataPointMap(interpreter, featureMap)
                            predictionList.add(DataPoint(
                                time = Date(predictedData.first),
                                value = predictedData.second
                            ))
                            if (predictionList.size > predictionBufferSize) {
                                savePredictionDataSeries(predictionSeries, predictionList)
                            }

                            onProgressUpdate( (currentStartTime.time - startTime.time)/(endTime.time - startTime.time).toFloat())

                            // Find smallest last value of all feature lists
                            currentStartTime = featureList.minOf { dataPointList -> dataPointList.last().time }
                        }
                        else {
                            continueLoop = false
                        }
                    }
                }
                interpreter.close()
            }
        }
    }

    private fun transformToMap(dataPointLists: List<List<DataPoint>>): Map<Long, Array<Float>> {
        return dataPointLists
            .flatten()
            .groupBy { it.time.time }
            .mapValues { (_, dataPoints) ->
                dataPoints.map { it.value }.toTypedArray()
            }
    }


    // Hinweis: Keine Seq2Seq-Modelle! Es wird nur der letzte Zeitstempel ausgegeben
    private fun inferenceOnDataPointMap(interpreter: Interpreter, dataPointMap: Map<Long, Array<Float>>) : Pair<Long, Float> {



        val inputVector = Array(1) { Array(50) { Array(2) { 0.0f } } }
        val outputVector = Array(1) { Array(50) { Array(1) { 0.0f } } }

        interpreter.run(inputVector, outputVector)

        return Pair(0, 0.0f)
    }


    private suspend fun savePredictionDataSeries(predictionSeries: DataSeries, predictedDataPointList: List<DataPoint>) {
        predictionSeries.id?.let { dataSeriesUseCase.addDataPoints(it, predictedDataPointList) }
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
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
import java.nio.FloatBuffer
import java.nio.MappedByteBuffer
import java.util.Date
import javax.inject.Inject

interface InferenceDataUseCase {
    suspend fun startInference(dataSet: DataSet, onProgressUpdate: (Float) -> Unit)
}

class InferenceDataUseCaseImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataSeriesUseCase: DataSeriesUseCase,
    private val aiModelUseCase: AIModelUseCase
) : InferenceDataUseCase {

    private val inferenceBufferSize = 10000
    private val predictionBufferSize = 1000

    override suspend fun startInference(dataSet: DataSet, onProgressUpdate: (Float) -> Unit) {
        dataSet.aiModel?.fileName?.let { fileName ->
            aiModelUseCase.openModelFile(context, fileName)?.let { fileContent ->
                Interpreter(fileContent)
            }?.let { interpreter ->
                dataSet.predictionSeries?.let { predictionSeries ->
                    val startTime = predictionSeries.startTime?: Date(0)
                    val endTime = predictionSeries.endTime?:Date(Long.MAX_VALUE)
                    var currentStartTime = startTime
                    var continueLoop = true
                    val featureList = mutableListOf<List<DataPoint>>()
                    val predictionList = mutableListOf<DataPoint>()

                    while(continueLoop) {
                        for (series in dataSet.columns) {
                            // TODO: inferenceBufferSize has to be controlled by the model batch size
                            dataSeriesUseCase.getDataPointsFromDataSeries(series.key, currentStartTime, inferenceBufferSize)?.let { dataPointList ->
                                if (dataPointList.isNotEmpty()) {
                                    featureList.add(dataPointList)
                                }
                            }
                        }

                        if (featureList.isNotEmpty()) {
                            val featureMap = transformToMap(featureList)

                            if (featureMap.isNotEmpty()) {
                                inferenceOnDataPointMap(interpreter, featureMap).let { predictedData ->
                                    predictionList.add(DataPoint(
                                        time = Date(predictedData.first),
                                        value = predictedData.second
                                    ))
                                }

                                if (predictionList.size > predictionBufferSize) {
                                    savePredictionDataSeries(predictionSeries, predictionList)
                                }
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
            .filter { (_, dataPoints) -> dataPoints.size == dataPointLists.size } // Incomplete Datapoints are discarded
            .mapValues { (_, dataPoints) ->
                dataPoints.map { it.value }.toTypedArray()
            }
    }


    private fun transformToInputVector(dataPointMap: Map<Long, Array<Float>>) : Array<FloatBuffer> {
        return dataPointMap.map { (time, features) -> FloatBuffer.wrap(floatArrayOf(time.toFloat()) + features.toFloatArray()) }
            .toTypedArray()
    }

    // Hinweis: Keine Seq2Seq-Modelle! Es wird nur der letzte Zeitstempel ausgegeben
    private fun inferenceOnDataPointMap(interpreter: Interpreter, dataPointMap: Map<Long, Array<Float>>) : Pair<Long, Float> {

        val inputVector = transformToInputVector(dataPointMap)
        val outputVector = floatArrayOf()

        interpreter.run(inputVector, outputVector)

        return Pair(dataPointMap.maxOf { it.key }, outputVector.last())
    }


    private suspend fun savePredictionDataSeries(predictionSeries: DataSeries, predictedDataPointList: List<DataPoint>) {
        predictionSeries.id?.let { dataSeriesUseCase.addDataPoints(it, predictedDataPointList) }
    }
}
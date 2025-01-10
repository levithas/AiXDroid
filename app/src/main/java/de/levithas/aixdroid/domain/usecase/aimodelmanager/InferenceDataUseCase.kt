package de.levithas.aixdroid.domain.usecase.aimodelmanager

import android.content.Context
import android.util.Log
import com.google.flatbuffers.FlatBufferBuilder.ByteBufferFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import de.levithas.aixdroid.domain.model.DataPoint
import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.domain.model.DataSet
import de.levithas.aixdroid.domain.usecase.datamanager.DataSeriesUseCase
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.util.Date
import javax.inject.Inject
import kotlin.math.cos
import kotlin.math.sin

interface InferenceDataUseCase {
    suspend fun startInference(dataSet: DataSet, onProgressUpdate: (Float) -> Unit)
}

class InferenceDataUseCaseImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataSeriesUseCase: DataSeriesUseCase,
    private val aiModelUseCase: AIModelUseCase
) : InferenceDataUseCase {

    private val inferenceBufferSize = 75 //Testmodell n_steps
    private val predictionBufferRatio = 0.5

    override suspend fun startInference(dataSet: DataSet, onProgressUpdate: (Float) -> Unit) {
        dataSet.aiModel?.fileName?.let { fileName ->
            aiModelUseCase.openModelFile(context, fileName)?.let { fileContent ->
                Interpreter(fileContent)
//                val options: Options = InterpreterApi.Options()
//                InterpreterApi.create(fileContent, options)
            }?.let { interpreter ->
                dataSet.predictionSeries?.let { predictionSeries ->

                    val dataSeriesWithTensor = dataSet.columns.filter { (_, tensor) -> tensor != null }
                    val startTime = dataSet.columns.minOf { it.key.startTime?.time ?:Long.MIN_VALUE }
                    val endTime = dataSet.columns.maxOf { it.key.endTime?.time ?:Long.MAX_VALUE }
                    var currentEndTime = endTime
                    var continueLoop = true

                    if (dataSeriesWithTensor.isNotEmpty()) {
                        while (continueLoop) {
                            val featureList = mutableListOf<List<DataPoint>>()
                            val predictionList = mutableListOf<DataPoint>()

                            for (series in dataSeriesWithTensor) {
                                // TODO: inferenceBufferSize has to be controlled by the model batch size
                                dataSeriesUseCase.getDataPointsFromDataSeries(series.key, Date(currentEndTime), inferenceBufferSize)?.let {
                                        dataPointList ->
                                    if (dataPointList.isNotEmpty()) {
                                        featureList.add(dataPointList.filter { dataPoint -> dataPoint.time.time >= startTime })
                                    }
                                }
                            }

                            if (featureList.isNotEmpty()) {
                                val featureMap = transformToMap(featureList)
                                if (featureMap.isNotEmpty()) {
                                    val timestamp = featureMap.maxOf { it.key }
                                    val featureVector = transformFeatureListToByteBufferArray(featureList)

                                    if (featureList[0].size == 75) {
                                        inferenceOnDataPointMap(interpreter, timestamp, featureVector)
                                            .let {
                                                    predictedData ->
                                                predictionList.add(
                                                    DataPoint(
                                                        time = Date(predictedData.first),
                                                        value = predictedData.second
                                                    )
                                                )
                                            }
                                        savePredictionDataSeries(predictionSeries, predictionList)
                                    }
                                    else {
                                        continueLoop = false
                                    }
                                }

                                onProgressUpdate((endTime - currentEndTime) / (endTime - startTime).toFloat())

                                // Find next larger Timepoint
                                currentEndTime = featureList.maxOf { dataSeries ->
                                    dataSeries.filter { dataPoint ->
                                        dataPoint.time.time < currentEndTime
                                    }.maxOfOrNull { it3 ->
                                        it3.time.time } ?: Long.MIN_VALUE
                                }
                            } else {
                                continueLoop = false
                            }
                        }
                    } else {
                        Log.e("InferenceDataUseCase", "No Tensors for dataSeries found!")
                    }
                }
                interpreter.close()
            }
        }
    }

    private fun transformFeatureListToByteBufferArray(featureList: List<List<DataPoint>>) : Array<ByteBuffer> {
        val bufferArray = mutableListOf<ByteBuffer>()
        featureList.forEach { list ->
            val byteBuffer = ByteBuffer.allocateDirect(4 * list.size )
            list.map {
                    point -> byteBuffer.putFloat(point.value)
            }
            byteBuffer.position(0)
            bufferArray.add(byteBuffer)
        }
        return bufferArray.toTypedArray()
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

    private fun transformTimePeriodicity(unixTime: Long, timePeriod: Float) : ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4*2) // 4 Byte per Value
        byteBuffer.putFloat(sin(2*Math.PI * unixTime/timePeriod).toFloat())
        byteBuffer.putFloat(cos(2*Math.PI * unixTime/timePeriod).toFloat())
        byteBuffer.position(0)
        return byteBuffer
    }

    // Hinweis: Keine Seq2Seq-Modelle! Es wird nur der letzte Zeitstempel ausgegeben
    private fun inferenceOnDataPointMap(interpreter: Interpreter, timeStamp: Long, inputFeatures: Array<ByteBuffer>): Pair<Long, Float> {
        val inputVector = arrayOf(transformTimePeriodicity(timeStamp, 24*3600000.0f)) + inputFeatures
        val outputVector = ByteBuffer.allocateDirect(4) // 4 Byte for 1 Output

        interpreter.run(inputVector[1], outputVector)

        return Pair(timeStamp, outputVector.getFloat(0))
    }


    private suspend fun savePredictionDataSeries(predictionSeries: DataSeries, predictedDataPointList: List<DataPoint>) {
        predictionSeries.id?.let { dataSeriesUseCase.addDataPoints(it, predictedDataPointList) }
    }
}
package de.levithas.aixdroid.domain.usecase.aimodelmanager

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import de.levithas.aixdroid.domain.model.DataPoint
import de.levithas.aixdroid.domain.model.DataSet
import de.levithas.aixdroid.domain.usecase.datamanager.DataSeriesUseCase
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.util.Date
import javax.inject.Inject
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class InferenceDataUseCaseImplV2 @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataSeriesUseCase: DataSeriesUseCase,
    private val aiModelUseCase: AIModelUseCase
) : InferenceDataUseCase {

    override suspend fun startInference(dataSet: DataSet, onProgressUpdate: (Float) -> Unit) {
        dataSet.aiModel?.let { aiModel ->
            aiModelUseCase.openModelFile(context, aiModel.fileName)?.let { fileContent ->
                Interpreter(fileContent).let { interpreter ->
                    dataSet.predictionSeries?.let { predictionSeries ->
                        aiModel.output?.let { output ->

                            val dataSeriesWithTensor = dataSet.columns.filter { it.value != null }
                            val startDate = dataSeriesWithTensor.minOf { it.key.startTime?.time ?:Long.MAX_VALUE }
                            val endDate = dataSeriesWithTensor.maxOf { it.key.endTime?.time ?:Long.MIN_VALUE }
                            val labelCount = output.shape[1]
                            val timePeriod = aiModel.timePeriod * 1000
                            val n_steps = aiModel.n_steps


                            // Iterating in days
                            for (currentDate in startDate until endDate step timePeriod.toLong()) {
                                val dataPointList = mutableListOf<DataPoint>()
                                val endTime = min(currentDate + timePeriod, endDate)

                                Log.i("InferenceDataUseCase", "Start Iteration from $currentDate to $endTime")

                                val inputData = dataSeriesWithTensor.map { dataSeriesUseCase.getDataPointsFromDataSeriesInDateRange(
                                    dataSeries = it.key,
                                    startTime = Date(currentDate),
                                    endTime = Date(endTime)
                                ) }

                                if (inputData.all { it != null }) {
                                    inputData.filterNotNull().let { notNullInputData ->

                                        // Merge Lists to one List
                                        // Group Elements according their timestamp
                                        // Filter times where not all values are present
                                        // Map Datapoints-Lists to FloatArrays
                                        val filteredData = notNullInputData.flatten().groupBy {
                                            it.time.time
                                        }.filter {
                                            it.value.size == dataSeriesWithTensor.size
                                        }

                                        // Merge times into dataArray
                                        val dataArrays = filteredData.mapValues { (timeStamp, dataPoints) ->
                                            getTimeDataArray(timeStamp, timePeriod) + dataPoints.map { it.value }
                                        }

                                        // Batching data
                                        for (currentTime in currentDate until endTime step 1000) {
                                            val dataBatch = dataArrays.filter {
                                                it.key >= currentTime && it.key < currentTime + n_steps*1000
                                            }.values.toTypedArray()

                                            if (dataBatch.size == n_steps) {

                                                val inputByteBuffer = getByteBufferFromDataBatch(dataBatch)
                                                val outputByteBuffer = mutableMapOf<Int, Any>()
                                                outputByteBuffer[0] = ByteBuffer.allocateDirect(labelCount * 4)

                                                interpreter.runForMultipleInputsOutputs(inputByteBuffer, outputByteBuffer)
                                                dataPointList.add(getDataPointFromByteBuffer(
                                                    outputByteBuffer[0] as ByteBuffer,
                                                    labelCount,
                                                    Date(currentTime)
                                                ))
                                            } else
                                            {
                                                Log.w("InferenceDataUseCase", "Not enough datapoints for Batch! n_steps: ${aiModel.n_steps} != " +
                                                        "${dataBatch.size}")
                                            }
                                            onProgressUpdate((currentTime-startDate) / (endDate-startDate).toFloat())
                                        }
                                    }
                                    Log.i("InferenceDataUseCase", "Process finished! ${dataPointList.size} new Datapoints")
                                    predictionSeries.id?.let { dataSeriesUseCase.addDataPoints(it, dataPointList) }
                                } else {
                                    Log.w("InferenceDataUseCase", "Datenreihe konnte nicht gefunden werden!")
                                }
                            }
                        }
                    }
                    interpreter.close()
                }
            }
        }
    }

    private fun getTimeDataArray(timestamp: Long, timePeriod: Int) : Array<Float> {
        return arrayOf(
            sin(timestamp * (2*Math.PI)/timePeriod.toFloat()).toFloat(),
            cos(timestamp * (2*Math.PI)/timePeriod.toFloat()).toFloat()
        )
    }


    private fun getByteBufferFromDataBatch(dataBatch: Array<Array<Float>>) : Array<ByteBuffer> {
        val byteBufferArray = mutableListOf<ByteBuffer>()

        dataBatch.forEach { data ->
            data.forEachIndexed { idx, entry ->
                if (byteBufferArray.size <= idx) {
                    byteBufferArray.add(ByteBuffer.allocate(dataBatch.size * 4))
                }
                byteBufferArray[idx % data.size].putFloat(entry)
            }
        }

        return byteBufferArray.toTypedArray()
    }

    private fun getSparseCategoricalCrossentropy(pseudoProbabilities: FloatArray) : Int {
        return pseudoProbabilities.withIndex().maxByOrNull { it.value }?.index?:-1
    }

    private fun getDataPointFromByteBuffer(byteBuffer: ByteBuffer, labelCount: Int, time: Date) : DataPoint {
        val floatList = mutableListOf<Float>()
        for (i in 0..labelCount) {
            floatList.add(byteBuffer.getFloat(i))
        }
        val result = getSparseCategoricalCrossentropy(floatList.toFloatArray())

        return DataPoint(value = result.toFloat(), time = time)
    }
}
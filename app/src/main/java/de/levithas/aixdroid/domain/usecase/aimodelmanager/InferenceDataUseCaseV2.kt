package de.levithas.aixdroid.domain.usecase.aimodelmanager

import android.content.Context
import android.util.Log
import com.google.flatbuffers.FlatBufferBuilder.ByteBufferFactory
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

    private val Iteration_Time = 24*3600L


    override suspend fun startInference(dataSet: DataSet, onProgressUpdate: (Float) -> Unit) {
        dataSet.aiModel?.let { aiModel ->
            aiModelUseCase.openModelFile(context, aiModel.fileName)?.let { fileContent ->
                Interpreter(fileContent).let { interpreter ->
                    dataSet.predictionSeries?.let { predictionSeries ->
                        aiModel.output?.let { output ->

                            val dataSeriesWithTensor = dataSet.columns.filter { it.value != null }
                            val startDate = dataSeriesWithTensor.minOf { it.key.startTime?.time ?:Long.MAX_VALUE }
                            val endDate = dataSeriesWithTensor.maxOf { it.key.endTime?.time ?:Long.MIN_VALUE }
                            val labelCount = output.shape[0]

                            // Iterating in days
                            for (currentDate in startDate until endDate step Iteration_Time) {
                                val dataPointList = mutableListOf<DataPoint>()
                                val endTime = min(currentDate + Iteration_Time, endDate)

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
                                            getTimeDataArray(timeStamp) + dataPoints.map { it.value }
                                        }

                                        // Batching data
                                        for (currentTime in currentDate until endTime) {
                                            val dataBatch = dataArrays.filter {
                                                it.key >= currentTime && it.key < currentTime + aiModel.n_steps
                                            }.values.toTypedArray()

                                            if (dataBatch.size == aiModel.n_steps) {

                                                val inputByteBuffer = getByteBufferFromDataBatch(dataBatch)
                                                val outputByteBuffer = ByteBuffer.allocateDirect(labelCount * 4)

                                                interpreter.run(inputByteBuffer, outputByteBuffer)
                                                dataPointList.add(getDataPointFromByteBuffer(outputByteBuffer, labelCount, Date(currentTime)))
                                            }
                                        }
                                    }
                                    predictionSeries.id?.let { dataSeriesUseCase.addDataPoints(it, dataPointList) }
                                } else {
                                    Log.w("InferenceDataUseCase", "Datenreihe konnte nicht gefunden werden!")
                                }

                                onProgressUpdate((currentDate-startDate) / (endDate-startDate).toFloat())
                            }
                        }
                    }
                    interpreter.close()
                }
            }
        }
    }

    private fun getTimeDataArray(timestamp: Long) : Array<Float> {
        return arrayOf(sin(timestamp.toFloat()), cos(timestamp.toFloat()))
    }


    private fun getByteBufferFromDataBatch(dataBatch: Array<Array<Float>>) : ByteBuffer {
        val byteBuffer = ByteBuffer.allocate(dataBatch.sumOf { it.size } * 4)

        dataBatch.forEach { data ->
            data.forEach { entry ->
                byteBuffer.putFloat(entry)
            }
        }

        return byteBuffer
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
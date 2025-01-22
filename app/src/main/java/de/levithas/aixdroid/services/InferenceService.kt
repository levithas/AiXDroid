package de.levithas.aixdroid.services

import android.app.Notification
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import de.levithas.aixdroid.data.repository.DataRepository
import de.levithas.aixdroid.domain.usecase.aimodelmanager.InferenceDataUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class InferenceService : AbstractService() {

    @Inject
    lateinit var dataRepository: DataRepository
    @Inject
    lateinit var inferenceDataUseCase: InferenceDataUseCase

    private val inferenceCheckDelayTime = 5000

    override fun onCreate() {
        super.onCreate()
        val notification = createNotification()
        startForeground(1, notification)

        Log.i("InferenceService", "Starting...")

        lifecycleScope.launch(Dispatchers.IO) {
            while (true) {
                Log.i("InferenceService", "Inference Cycle Start")
                val startTime = System.currentTimeMillis()
                dataRepository.getAllDataSeries()
                    .collect { dataSeriesList ->
                        if (dataSeriesList.isEmpty()) {
                            Log.w("InferenceService", "List empty...")
                        }

                        dataRepository.getAllDataSetsWithAutoInference()
                            .collect { dataSetList ->
                                dataSetList.forEach { dataSet ->
                                    inferenceDataUseCase.startInference(
                                        dataSet = dataSet,
                                        onProgressUpdate = { value ->
                                            onProgressUpdate(
                                                dataSet.name,
                                                value
                                            )
                                        },
                                    )
                                }
                            }
                    }

                val elapsedTime = System.currentTimeMillis() - startTime
                delay(maxOf(0, inferenceCheckDelayTime - elapsedTime))
                Log.i("InferenceService", "Inference Cycle End")
            }
        }
    }

    private fun onProgressUpdate(dataSetName: String, value: Float) {
        Log.i("InferenceService", "AutoPredict progress for ${dataSetName}: ${value}")
    }

    private fun createNotification(): Notification {
        val notificationChannel = getNotificationChannel()

        return NotificationCompat.Builder(this, notificationChannel.id)
            .setContentTitle("Inference Service")
            .setContentText("Service is running!")
            .build()
    }
}
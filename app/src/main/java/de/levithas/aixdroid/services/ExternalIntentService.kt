package de.levithas.aixdroid.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import de.levithas.aixdroid.R
import de.levithas.aixdroid.data.repository.ExternalIntentRepository
import de.levithas.aixdroid.domain.model.ExternalIntentConfiguration
import de.levithas.aixdroid.services.intentactions.InferenceExternalIntentAction
import de.levithas.aixdroid.services.intentactions.ReadDataExternalIntentAction
import de.levithas.aixdroid.services.intentactions.WriteDataExternalIntentAction
import de.levithas.aixdroid.services.receivers.ExternalIntentReceiver
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ExternalIntentService : LifecycleService() {

    @Inject
    lateinit var intentRepository: ExternalIntentRepository
    @Inject
    lateinit var readDataAction: ReadDataExternalIntentAction
    @Inject
    lateinit var writeDataAction: WriteDataExternalIntentAction
    @Inject
    lateinit var inferenceAction: InferenceExternalIntentAction

    private val intentReceiverList = mutableListOf<ExternalIntentReceiver>()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate() {
        super.onCreate()
        val notification = createNotification()
        startForeground(1, notification)

        lifecycleScope.launch {
            intentRepository.getExternalIntentList().asLiveData().observeForever { intentDataList ->
                intentReceiverList.forEach { entry ->
                    unregisterReceiver(entry)
                    Log.i("ExternalIntentService", "Unregister ${entry.getActionString()}")
                }
                intentReceiverList.clear()

                updateIntents(intentDataList)

                intentReceiverList.forEach { intentReceiver ->
                    val filter = IntentFilter(intentReceiver.getActionString())
                    registerReceiver(intentReceiver, filter, Context.RECEIVER_EXPORTED)
                    Log.i("ExternalIntentService", "Registered ${intentReceiver.getActionString()}")
                }
            }
        }
    }

    private fun updateIntents(intentConfigurationList: List<ExternalIntentConfiguration>) {
        intentConfigurationList.forEach { intentConfiguration ->
            if (intentConfiguration.allowReadData) {
                intentReceiverList.add(ExternalIntentReceiver(intentConfiguration.packageName, readDataAction))
            }
            if (intentConfiguration.allowWriteData) {
                intentReceiverList.add(ExternalIntentReceiver(intentConfiguration.packageName, writeDataAction))
            }
            if (intentConfiguration.allowInference) {
                intentReceiverList.add(ExternalIntentReceiver(intentConfiguration.packageName, inferenceAction))
            }
        }
    }

    private fun createNotification(): Notification {
        val notificationChannelId = "service_channel"

        val channel = NotificationChannel(
            notificationChannelId,
            "Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Service läuft im Hintergrund")
            .setContentText("Der Service ist aktiv.")
            .build()
    }
}
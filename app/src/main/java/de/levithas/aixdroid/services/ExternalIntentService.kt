package de.levithas.aixdroid.services

import android.content.IntentFilter
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import de.levithas.aixdroid.data.repository.ExternalIntentRepository
import de.levithas.aixdroid.domain.model.ExternalIntentConfiguration
import de.levithas.aixdroid.services.intentactions.InferenceExternalIntentAction
import de.levithas.aixdroid.services.intentactions.ReadDataExternalIntentAction
import de.levithas.aixdroid.services.intentactions.WriteDataExternalIntentAction
import de.levithas.aixdroid.services.receivers.ExternalIntentReceiver
import kotlinx.coroutines.launch
import javax.inject.Inject

class ExternalIntentService @Inject constructor(
    private val intentRepository: ExternalIntentRepository,
    private val readDataAction: ReadDataExternalIntentAction,
    private val writeDataAction: WriteDataExternalIntentAction,
    private val inferenceAction: InferenceExternalIntentAction
) : LifecycleService() {

    private val intentReceiverList = mutableListOf<ExternalIntentReceiver>()

    override fun onCreate() {
        super.onCreate()
        lifecycleScope.launch {
            intentRepository.getExternalIntentList().asLiveData().observeForever { intentDataList ->
                intentReceiverList.forEach { entry -> unregisterReceiver(entry) }
                intentReceiverList.clear()

                updateIntents(intentDataList)

                intentReceiverList.forEach { intentReceiver ->
                    val filter = IntentFilter(intentReceiver.getActionString())
                    registerReceiver(intentReceiver, filter)
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
}
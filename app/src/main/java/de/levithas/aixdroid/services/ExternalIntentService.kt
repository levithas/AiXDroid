package de.levithas.aixdroid.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.asLiveData
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import de.levithas.aixdroid.data.repository.DataRepository
import de.levithas.aixdroid.data.repository.ExternalIntentRepository
import de.levithas.aixdroid.domain.model.ExternalIntentConfiguration
import de.levithas.aixdroid.services.intentactions.ExternalIntentAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExternalIntentService @Inject constructor(
    private var intentRepository: ExternalIntentRepository,
    private var dataRepository: DataRepository
) : LifecycleService() {

    private val intentMap = mutableMapOf<String, BroadcastReceiver>()

    override fun onCreate() {
        super.onCreate()
        lifecycleScope.launch {
            intentRepository.getExternalIntentList().asLiveData().observeForever { intentDataList ->
                intentMap.forEach { entry -> unregisterReceiver(entry.value) }

                updateIntents(intentDataList)

                intentMap.forEach { entry ->
                    val filter = IntentFilter(entry.key)
                    registerReceiver(entry.value, filter)
                }
            }
        }
    }

    private fun updateIntents(intentConfigurationList: List<ExternalIntentConfiguration>) {
        intentConfigurationList.forEach { item ->
            if (item.allowReadData) {
                intentMap
            }

        }
    }

    private fun createReceiver(intentData: IntentData): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                // Verarbeite den empfangenen Intent basierend auf dem IntentData
                processExternalIntents(intent)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        processExternalIntents(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun processExternalIntents(intent: Intent?) {
        intent?.let {

        }
    }
}
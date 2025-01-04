package de.levithas.aixdroid

import android.app.Application
import android.content.Intent
import androidx.core.content.ContextCompat
import dagger.hilt.android.HiltAndroidApp
import de.levithas.aixdroid.services.ExternalIntentService
import de.levithas.aixdroid.services.InferenceService

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startExternalIntentService()
        startInferenceService()
    }

    private fun startInferenceService() {
        val service = Intent(this, InferenceService::class.java)
        ContextCompat.startForegroundService(this, service)
    }

    private fun startExternalIntentService() {
        val service = Intent(this, ExternalIntentService::class.java)
        ContextCompat.startForegroundService(this, service)
    }
}
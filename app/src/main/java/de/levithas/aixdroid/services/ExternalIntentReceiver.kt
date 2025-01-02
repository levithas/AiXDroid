package de.levithas.aixdroid.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat

class ExternalIntentReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val service = Intent(it, ExternalIntentService::class.java).apply {
                action = intent?.action
                putExtras(intent?.extras?: Bundle())
            }
            ContextCompat.startForegroundService(it, service)
        }
    }
}
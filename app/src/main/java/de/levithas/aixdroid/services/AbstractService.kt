package de.levithas.aixdroid.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import androidx.lifecycle.LifecycleService
import dagger.hilt.android.AndroidEntryPoint
import de.levithas.aixdroid.R

abstract class AbstractService : LifecycleService() {

    protected fun getNotificationChannel(): NotificationChannel {
        val notificationChannelId = getString(R.string.notification_channelId)
        val manager = getSystemService(NotificationManager::class.java)
        var channel = manager?.getNotificationChannel(notificationChannelId)
        if (channel == null) {
            channel = NotificationChannel(
                notificationChannelId,
                "Inference Service",
                NotificationManager.IMPORTANCE_LOW
            )
            manager?.createNotificationChannel(channel)
        }
        return channel
    }
}
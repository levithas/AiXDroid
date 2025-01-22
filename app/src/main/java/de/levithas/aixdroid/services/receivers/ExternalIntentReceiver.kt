package de.levithas.aixdroid.services.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import de.levithas.aixdroid.domain.usecase.intentmanager.intentactions.ExternalIntentAction

class ExternalIntentReceiver(
    private val actionString: String,
    private val intentAction: ExternalIntentAction
) : BroadcastReceiver() {

    fun getActionString() = actionString + intentAction.getActionString()

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("ExternalIntentReceiver", "Received Intent for ${getActionString()}")
        intentAction.process(context, intent)
    }
}
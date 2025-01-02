package de.levithas.aixdroid.services.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import de.levithas.aixdroid.services.intentactions.ExternalIntentAction

class ExternalIntentReceiver(
    private val actionString: String,
    private val intentAction: ExternalIntentAction
) : BroadcastReceiver() {

    fun getActionString() = actionString + intentAction.getActionString()

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            // TODO: Fill DataBundle
            intentAction.process(Bundle())
        }
    }
}
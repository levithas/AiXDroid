package de.levithas.aixdroid.services.intentactions

import android.content.Context
import android.content.Intent
import android.os.Bundle

interface ExternalIntentAction {
    fun getActionString() : String
    fun process(context: Context?, intent: Intent?)
}
package de.levithas.aixdroid.services.intentactions

import android.os.Bundle

interface ExternalIntentAction {
    fun getActionString() : String
    fun process(data: Bundle)
}
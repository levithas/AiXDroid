package de.levithas.aixdroid.services.intentactions

import android.os.Bundle

interface ExternalIntentAction {
    fun intentActionString() : String
    fun process(data: Bundle)
}
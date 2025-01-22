package de.levithas.aixdroid.domain.usecase.intentmanager.intentactions

import android.content.Context
import android.content.Intent

interface ExternalIntentAction {
    fun getActionString() : String
    fun process(context: Context?, intent: Intent?)
}
package de.levithas.aixdroid.domain.usecase.intentmanager.intentactions

import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import de.levithas.aixdroid.data.repository.DataRepository
import javax.inject.Inject

class InferenceExternalIntentAction @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataRepository: DataRepository,
) : ExternalIntentAction {

    override fun getActionString() = ".INFERENCE"

    override fun process(context: Context?, intent: Intent?) {
        Log.i("InferenceExternalIntentAction", "Processing...")
    }
}
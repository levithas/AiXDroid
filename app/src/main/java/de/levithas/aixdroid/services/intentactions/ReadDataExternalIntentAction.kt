package de.levithas.aixdroid.services.intentactions

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import de.levithas.aixdroid.data.repository.DataRepository
import javax.inject.Inject


class ReadDataExternalIntentAction @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataRepository: DataRepository,
    ) : ExternalIntentAction {

    override fun getActionString() = ".READ_DATA"

    override fun process(context: Context, intent: Intent?) {
        Log.i("ReadDataExternalIntentAction", "Processing...")
    }
}
package de.levithas.aixdroid.services.intentactions

import android.content.Context
import android.os.Bundle
import dagger.hilt.android.qualifiers.ApplicationContext
import de.levithas.aixdroid.data.repository.DataRepository
import javax.inject.Inject


class ReadDataExternalIntentAction @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataRepository: DataRepository,
    ) : ExternalIntentAction {
    override fun intentActionString(): String = "de.levithas.AiXDroid.READ_DATA"

    override fun process(data: Bundle) {

    }
}
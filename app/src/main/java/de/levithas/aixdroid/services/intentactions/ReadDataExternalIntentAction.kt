package de.levithas.aixdroid.services.intentactions

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import de.levithas.aixdroid.R
import de.levithas.aixdroid.data.repository.DataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class ReadDataExternalIntentAction @Inject constructor(
    @ApplicationContext private val localContext: Context,
    private val dataRepository: DataRepository,
    ) : ExternalIntentAction {

    override fun getActionString() = ".READ_DATA"

    override fun process(context: Context?, intent: Intent?) {
        Log.i("ReadDataExternalIntentAction", "Processing...")
        intent?.extras?.let { extras ->
            val dataSeriesName = extras.getString("seriesName")
            val lastTimeValue = extras.getLong("lastTimeValue")
            val dataCount = extras.getInt("dataCount")

            if (dataSeriesName != null && lastTimeValue > 0 && dataCount > 0) {
                CoroutineScope(Dispatchers.IO).launch {
                    val responseIntent = Intent()
                    responseIntent.setAction(localContext.getString(R.string.aixdroid_response_action))
                    val dataSeries = dataRepository.getDataSeriesByName(dataSeriesName)
                    val responseExtras = Bundle()
                    if (dataSeries != null) {
                        dataSeries.id?.let {
                            val dataPointList = dataRepository.getDataPointsByDataSeriesId(it, lastTimeValue.toLong(), dataCount)
                            val timeValuesArray = mutableListOf<Long>()
                            val dataValuesArray = mutableListOf<Float>()

                            dataPointList.forEach { point ->
                                timeValuesArray.add(point.time.time)
                                dataValuesArray.add(point.value)
                            }
                            responseExtras.putLongArray("timeValues", timeValuesArray.toLongArray())
                            responseExtras.putFloatArray("dataValues", dataValuesArray.toFloatArray())

                            Log.w("WriteDataExternalIntentAction", "Sending ${timeValuesArray.size} Datapoints...")
                        }
                    } else {
                        responseExtras.putString("error", "DataSeries does not exist")
                        Log.w("WriteDataExternalIntentAction", "DataSeries does not exist! (${dataSeriesName})")
                    }
                    responseIntent.putExtras(responseExtras)
                    localContext.sendBroadcast(responseIntent)
                    Log.w("WriteDataExternalIntentAction", "Done!")
                }
            } else {
                Log.w("WriteDataExternalIntentAction", "Missing required extras!")
            }
        }
    }
}
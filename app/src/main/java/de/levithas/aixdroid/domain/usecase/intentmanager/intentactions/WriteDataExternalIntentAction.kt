package de.levithas.aixdroid.domain.usecase.intentmanager.intentactions

import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import de.levithas.aixdroid.domain.model.DataPoint
import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.domain.usecase.datamanager.DataSeriesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


class WriteDataExternalIntentAction @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataSeriesUseCase: DataSeriesUseCase
    ) : ExternalIntentAction {

    override fun getActionString() = ".WRITE_DATA"

    override fun process(context: Context?, intent: Intent?) {
        Log.i("WriteDataExternalIntentAction", "Processing...")
        intent?.extras?.let { extras ->
            val dataSeriesName = extras.getString("seriesName")
            val timeValueArray = extras.getLongArray("timeValues")
            val dataValueArray = extras.getFloatArray("dataValues")

            if (dataSeriesName != null && timeValueArray != null && dataValueArray != null
                && dataSeriesName.isNotBlank() && timeValueArray.size == dataValueArray.size
                && timeValueArray.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val dataPointList = timeValueArray.mapIndexed { index, time ->
                        DataPoint(value = dataValueArray[index], time = Date(time))
                    }

                    val dataSeries = dataSeriesUseCase.getDataSeriesByName(dataSeriesName).first();
                    var dataSeriesId = dataSeries?.id
                    if (dataSeriesId == null) {
                        Log.i("WriteDataExternalIntentAction", "No dataSeries found with name ${dataSeriesName}! Creating one...")
                        dataSeriesId = dataSeriesUseCase.addDataSeries(
                            DataSeries(
                                id = null,
                                origin = "External Intent",
                                name = dataSeriesName,
                                unit = "",
                                count = null,
                                startTime = null,
                                endTime = null,
                            )
                        )
                    } else
                    {
                        Log.i("WriteDataExternalIntentAction", "DataSeries found!")
                    }

                    dataSeriesUseCase.addDataPoints(dataSeriesId, dataPointList)
                    Log.i("WriteDataExternalIntentAction", "Done writing ${dataPointList.size} DataPoints!")
                }
            } else {
                Log.w("WriteDataExternalIntentAction", "Missing required extras!")
            }
        }
    }
}
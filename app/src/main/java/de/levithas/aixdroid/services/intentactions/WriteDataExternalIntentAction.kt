package de.levithas.aixdroid.services.intentactions

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import de.levithas.aixdroid.data.repository.DataRepository
import de.levithas.aixdroid.domain.model.DataPoint
import de.levithas.aixdroid.domain.model.DataSeries
import de.levithas.aixdroid.domain.usecase.datamanager.DataSeriesUseCase
import de.levithas.aixdroid.domain.usecase.datamanager.DataSetUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


class WriteDataExternalIntentAction @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataSeriesUseCase: DataSeriesUseCase
    ) : ExternalIntentAction {

    override fun getActionString() = ".WRITE_DATA"

    override fun process(context: Context, intent: Intent?) {
        Log.i("WriteDataExternalIntentAction", "Processing...")
        intent?.extras?.let { extras ->
            val dataSeriesName = extras.getString("seriesName")
            val timeValueArray = extras.getIntegerArrayList("timestamp")
            val dataValueArray = extras.getFloatArray("data")

            if (dataSeriesName != null && timeValueArray != null && dataValueArray != null
                && dataSeriesName.isNotBlank() && timeValueArray.size == dataValueArray.size
                && timeValueArray.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val dataPointList = timeValueArray.mapIndexed { index, time ->
                        DataPoint(value = dataValueArray[index], time = Date(time.toLong()))
                    }

                    val dataSeries = dataSeriesUseCase.getDataSeriesByName(dataSeriesName)
                    var dataSeriesId = dataSeries?.id
                    dataSeries?.let {
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
                    }

                    dataSeriesId?.let { id -> dataSeriesUseCase.addDataPoints(id, dataPointList) }
                }
            } else {
                Log.w("WriteDataExternalIntentAction", "Missing required extras!")
            }
        }
    }
}
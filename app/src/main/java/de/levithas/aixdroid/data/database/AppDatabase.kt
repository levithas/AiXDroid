package de.levithas.aixdroid.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import de.levithas.aixdroid.data.dao.DataSetDao
import de.levithas.aixdroid.data.dao.ModelDataDao
import de.levithas.aixdroid.data.model.DBDataSet
import de.levithas.aixdroid.data.model.DBDataSetToTimeSeries
import de.levithas.aixdroid.data.model.DBModelData
import de.levithas.aixdroid.data.model.DBModelDataInput
import de.levithas.aixdroid.data.model.DBModelDataOutput
import de.levithas.aixdroid.data.model.DBTensorData
import de.levithas.aixdroid.data.model.DBTimeSeries
import de.levithas.aixdroid.data.model.DBTimeSeriesDataPoint
import de.levithas.aixdroid.data.model.DBTimeSeriesToDataPoint

@Database(entities = [
    DBModelData::class,
    DBTensorData::class,
    DBModelDataInput::class,
    DBModelDataOutput::class,

    DBDataSet::class,
    DBTimeSeries::class,
    DBTimeSeriesDataPoint::class,
    DBDataSetToTimeSeries::class,
    DBTimeSeriesToDataPoint::class,

                     ], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ModelDataDao(): ModelDataDao
    abstract fun DataSetDao(): DataSetDao
}
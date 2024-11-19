package de.levithas.aixdroid.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import de.levithas.aixdroid.data.dao.DatasetDao
import de.levithas.aixdroid.data.dao.ModelDataDao
import de.levithas.aixdroid.data.model.DBDataset
import de.levithas.aixdroid.data.model.DBDatasetToTimeSeries
import de.levithas.aixdroid.data.model.DBModelData
import de.levithas.aixdroid.data.model.DBModelDataInput
import de.levithas.aixdroid.data.model.DBModelDataOutput
import de.levithas.aixdroid.data.model.DBTensorData
import de.levithas.aixdroid.data.model.DBTimeSeries
import de.levithas.aixdroid.data.model.DBTimeSeriesDataPoint

@Database(entities = [
    DBModelData::class,
    DBTensorData::class,
    DBModelDataInput::class,
    DBModelDataOutput::class,

    DBDataset::class,
    DBTimeSeries::class,
    DBDatasetToTimeSeries::class,
    DBTimeSeriesDataPoint::class,

                     ], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ModelDataDao(): ModelDataDao
    abstract fun DatasetDao(): DatasetDao
}
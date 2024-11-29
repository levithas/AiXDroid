package de.levithas.aixdroid.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import de.levithas.aixdroid.data.dao.DataSetDao
import de.levithas.aixdroid.data.dao.ModelDataDao
import de.levithas.aixdroid.data.model.data.DBDataSet
import de.levithas.aixdroid.data.model.data.DBDataSetToDataSeries
import de.levithas.aixdroid.data.model.ai.DBModelData
import de.levithas.aixdroid.data.model.ai.DBModelDataInput
import de.levithas.aixdroid.data.model.ai.DBModelDataOutput
import de.levithas.aixdroid.data.model.ai.DBTensorData
import de.levithas.aixdroid.data.model.data.DBDataSeries
import de.levithas.aixdroid.data.model.data.DBDataPoint
import de.levithas.aixdroid.data.model.data.DBDataSetToModelData

@Database(entities = [
    DBModelData::class,
    DBTensorData::class,
    DBModelDataInput::class,
    DBModelDataOutput::class,

    DBDataSet::class,
    DBDataSeries::class,
    DBDataPoint::class,
    DBDataSetToDataSeries::class,
    DBDataSetToModelData::class

                     ], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ModelDataDao(): ModelDataDao
    abstract fun DataSetDao(): DataSetDao
}
package de.levithas.aixdroid.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import de.levithas.aixdroid.data.dao.ModelDataDao
import de.levithas.aixdroid.data.model.DBModelData
import de.levithas.aixdroid.data.model.DBModelDataInput
import de.levithas.aixdroid.data.model.DBModelDataOutput
import de.levithas.aixdroid.data.model.DBTensorData

@Database(entities = [
    DBModelData::class,
    DBTensorData::class,
    DBModelDataInput::class,
    DBModelDataOutput::class
                     ], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ModelDataDao(): ModelDataDao
}
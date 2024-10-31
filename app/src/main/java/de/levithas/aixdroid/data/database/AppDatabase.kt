package de.levithas.aixdroid.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import de.levithas.aixdroid.data.dao.AIModelDao
import de.levithas.aixdroid.data.model.AIModelConfigurationEntity

@Database(entities = [AIModelConfigurationEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun AIModelDao(): AIModelDao
}
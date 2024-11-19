package de.levithas.aixdroid.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.levithas.aixdroid.data.dao.DatasetDao
import de.levithas.aixdroid.data.dao.ModelDataDao
import de.levithas.aixdroid.data.database.AppDatabase
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    fun provideModelDataDao(database: AppDatabase): ModelDataDao {
        return database.ModelDataDao()
    }

    @Provides
    fun provideDatasetDao(database: AppDatabase): DatasetDao {
        return database.DatasetDao()
    }
}
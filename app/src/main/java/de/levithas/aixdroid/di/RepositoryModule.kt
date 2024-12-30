package de.levithas.aixdroid.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.levithas.aixdroid.data.dao.DataSetDao
import de.levithas.aixdroid.data.dao.IntentDataDao
import de.levithas.aixdroid.data.dao.ModelDataDao
import de.levithas.aixdroid.data.repository.DataRepository
import de.levithas.aixdroid.data.repository.DataRepositoryImpl
import de.levithas.aixdroid.data.repository.ExternalIntentRepository
import de.levithas.aixdroid.data.repository.ExternalIntentRepositoryImpl
import de.levithas.aixdroid.data.repository.ModelRepository
import de.levithas.aixdroid.data.repository.ModelRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideModelRepository(
        dao: ModelDataDao
    ) : ModelRepository {
        return ModelRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideDataRepository(
        dao: DataSetDao,
        modelDao: ModelDataDao
    ) : DataRepository {
        return DataRepositoryImpl(dao, provideModelRepository(modelDao))
    }

    @Provides
    @Singleton
    fun provideExternalIntentRepository(
        dao: IntentDataDao
    ) : ExternalIntentRepository {
        return ExternalIntentRepositoryImpl(dao)
    }
}
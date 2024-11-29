package de.levithas.aixdroid.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.levithas.aixdroid.data.dao.DataSetDao
import de.levithas.aixdroid.data.dao.ModelDataDao
import de.levithas.aixdroid.domain.repository.DataRepository
import de.levithas.aixdroid.domain.repository.DataRepositoryImpl
import de.levithas.aixdroid.domain.repository.ModelRepository
import de.levithas.aixdroid.domain.repository.ModelRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

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
}
package de.levithas.aixdroid.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.levithas.aixdroid.data.dao.AIModelDao
import de.levithas.aixdroid.domain.repository.ModelRepository
import de.levithas.aixdroid.domain.repository.ModelRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideModelRepository(
        dao: AIModelDao
    ) : ModelRepository {
        return ModelRepositoryImpl(dao)
    }
}
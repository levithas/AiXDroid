package de.levithas.aixdroid.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.levithas.aixdroid.data.repository.ModelRepository
import de.levithas.aixdroid.domain.usecase.aimodelmanager.AIModelUseCase
import de.levithas.aixdroid.domain.usecase.aimodelmanager.GetModelListUseCase


@Module
@InstallIn(SingletonComponent::class)
object TestUseCaseModule {

    @Provides
    fun provideAddNewAIModelUseCase(
        repository: ModelRepository
    ): AIModelUseCase {
        TODO("")
    }

    @Provides
    fun provideGetModelListUseCase(
        repository: ModelRepository
    ): GetModelListUseCase {
        TODO()
    }
}
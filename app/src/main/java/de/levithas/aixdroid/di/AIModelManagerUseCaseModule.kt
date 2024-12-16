package de.levithas.aixdroid.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.levithas.aixdroid.data.repository.ModelRepository
import de.levithas.aixdroid.domain.usecase.aimodelmanager.AIModelUseCase
import de.levithas.aixdroid.domain.usecase.aimodelmanager.AIModelUseCaseImpl
import de.levithas.aixdroid.domain.usecase.aimodelmanager.GetModelListUseCase
import de.levithas.aixdroid.domain.usecase.aimodelmanager.GetModelListUseCaseImpl


@Module
@InstallIn(SingletonComponent::class)
object AIModelManagerUseCaseModule {

    @Provides
    fun provideAIModelUseCase(
        repository: ModelRepository
    ): AIModelUseCase {
        return AIModelUseCaseImpl(
            repository
        )
    }

    @Provides
    fun provideGetModelListUseCase(
        repository: ModelRepository
    ): GetModelListUseCase {
        return GetModelListUseCaseImpl(repository)
    }
}
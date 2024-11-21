package de.levithas.aixdroid.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.levithas.aixdroid.domain.repository.ModelRepository
import de.levithas.aixdroid.domain.usecase.aimodelmanager.AddNewAIModelUseCase
import de.levithas.aixdroid.domain.usecase.aimodelmanager.AddNewAIModelUseCaseImpl
import de.levithas.aixdroid.domain.usecase.aimodelmanager.DeleteModelUseCase
import de.levithas.aixdroid.domain.usecase.aimodelmanager.DeleteModelUseCaseImpl
import de.levithas.aixdroid.domain.usecase.aimodelmanager.GetModelListUseCase
import de.levithas.aixdroid.domain.usecase.aimodelmanager.GetModelListUseCaseImpl
import de.levithas.aixdroid.domain.usecase.datamanager.ReadTextFileUseCase
import de.levithas.aixdroid.domain.usecase.datamanager.ReadTextFileUseCaseImpl


@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideAddNewAIModelUseCase(
        repository: ModelRepository
    ): AddNewAIModelUseCase {
        return AddNewAIModelUseCaseImpl(
            repository
        )
    }

    @Provides
    fun provideDeleteModelUsecase(
        repository: ModelRepository
    ): DeleteModelUseCase {
        return DeleteModelUseCaseImpl(repository)
    }

    @Provides
    fun provideGetModelListUseCase(
        repository: ModelRepository
    ): GetModelListUseCase {
        return GetModelListUseCaseImpl(repository)
    }

    @Provides
    fun provideReadTextFileUseCase(): ReadTextFileUseCase {
        return ReadTextFileUseCaseImpl()
    }
}
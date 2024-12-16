package de.levithas.aixdroid.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.levithas.aixdroid.data.repository.DataRepository
import de.levithas.aixdroid.data.repository.ModelRepository
import de.levithas.aixdroid.domain.usecase.aimodelmanager.AIModelUseCaseImpl
import de.levithas.aixdroid.domain.usecase.aimodelmanager.InferenceDataUseCase
import de.levithas.aixdroid.domain.usecase.aimodelmanager.InferenceDataUseCaseImpl
import de.levithas.aixdroid.domain.usecase.datamanager.DataSeriesUseCase
import de.levithas.aixdroid.domain.usecase.datamanager.DataSeriesUseCaseImpl
import de.levithas.aixdroid.domain.usecase.datamanager.DataSetUseCase
import de.levithas.aixdroid.domain.usecase.datamanager.DataSetUseCaseImpl
import de.levithas.aixdroid.domain.usecase.datamanager.ExportDataUseCase
import de.levithas.aixdroid.domain.usecase.datamanager.ExportDataUseCaseImpl
import de.levithas.aixdroid.domain.usecase.datamanager.GetDataListsUseCase
import de.levithas.aixdroid.domain.usecase.datamanager.GetDataListsUseCaseImpl


@Module
@InstallIn(SingletonComponent::class)
object DataManagerUseCaseModule {

    @Provides
    fun provideGetDataSeriesListUseCase(
        repository: DataRepository
    ): GetDataListsUseCase {
        return GetDataListsUseCaseImpl(repository)
    }

    @Provides
    fun provideExportDataUseCase(
        repository: DataRepository
    ): ExportDataUseCase {
        return ExportDataUseCaseImpl(repository)
    }

    @Provides
    fun provideDataSeriesUseCase(
        context: Context,
        repository: DataRepository
    ): DataSeriesUseCase {
        return DataSeriesUseCaseImpl(
            context,
            repository,
            DataSetUseCaseImpl(repository)
        )
    }

    @Provides
    fun provideDataSetUseCase(
        repository: DataRepository
    ): DataSetUseCase {
        return DataSetUseCaseImpl(repository)
    }

    @Provides
    fun providesInferenceUseCase(
        context: Context,
        dataRepository: DataRepository,
        modelRepository: ModelRepository
    ): InferenceDataUseCase {
        return InferenceDataUseCaseImpl(
            context, DataSeriesUseCaseImpl(
                context,
                dataRepository,
                DataSetUseCaseImpl(dataRepository)
            ),
            AIModelUseCaseImpl(modelRepository)
        )
    }
}
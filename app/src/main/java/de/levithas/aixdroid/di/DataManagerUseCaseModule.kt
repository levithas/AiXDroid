package de.levithas.aixdroid.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.levithas.aixdroid.domain.repository.DataRepository
import de.levithas.aixdroid.domain.repository.ModelRepository
import de.levithas.aixdroid.domain.usecase.datamanager.DeleteDataSeriesUseCase
import de.levithas.aixdroid.domain.usecase.datamanager.DeleteDataSeriesUseCaseImpl
import de.levithas.aixdroid.domain.usecase.datamanager.ExportDataUseCase
import de.levithas.aixdroid.domain.usecase.datamanager.ExportDataUseCaseImpl
import de.levithas.aixdroid.domain.usecase.datamanager.GetDataSeriesListUseCase
import de.levithas.aixdroid.domain.usecase.datamanager.GetDataSeriesListUseCaseImpl
import de.levithas.aixdroid.domain.usecase.datamanager.ImportDataUseCase
import de.levithas.aixdroid.domain.usecase.datamanager.ImportDataUseCaseImpl
import de.levithas.aixdroid.domain.usecase.datamanager.ReadTextFileUseCase
import de.levithas.aixdroid.domain.usecase.datamanager.ReadTextFileUseCaseImpl


@Module
@InstallIn(SingletonComponent::class)
object DataManagerUseCaseModule {

    @Provides
    fun provideReadTextFileUseCase(): ReadTextFileUseCase {
        return ReadTextFileUseCaseImpl()
    }

    @Provides
    fun provideGetDataSeriesListUseCase(
        repository: DataRepository
    ): GetDataSeriesListUseCase {
        return GetDataSeriesListUseCaseImpl(repository)
    }

     @Provides
     fun provideImportDataUseCase(
         repository: DataRepository
     ) : ImportDataUseCase {
         return ImportDataUseCaseImpl(
             repository,
             provideReadTextFileUseCase()
         )
     }

    @Provides
    fun provideExportDataUseCase(
        repository: DataRepository
    ): ExportDataUseCase {
        return ExportDataUseCaseImpl(repository)
    }

    @Provides
    fun provideDeleteDataSeriesUseCase(
        repository: DataRepository
    ): DeleteDataSeriesUseCase {
        return DeleteDataSeriesUseCaseImpl(repository)
    }
}
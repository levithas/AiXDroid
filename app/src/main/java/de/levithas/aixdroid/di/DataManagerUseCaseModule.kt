package de.levithas.aixdroid.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.levithas.aixdroid.domain.repository.DataRepository
import de.levithas.aixdroid.domain.usecase.datamanager.DeleteDataSeriesUseCase
import de.levithas.aixdroid.domain.usecase.datamanager.DeleteDataSeriesUseCaseImpl
import de.levithas.aixdroid.domain.usecase.datamanager.ExportDataUseCase
import de.levithas.aixdroid.domain.usecase.datamanager.ExportDataUseCaseImpl
import de.levithas.aixdroid.domain.usecase.datamanager.GetDataListsUseCase
import de.levithas.aixdroid.domain.usecase.datamanager.GetDataListsUseCaseImpl
import de.levithas.aixdroid.domain.usecase.datamanager.ImportDataUseCase
import de.levithas.aixdroid.domain.usecase.datamanager.ImportDataUseCaseImpl


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
     fun provideImportDataUseCase(
         repository: DataRepository
     ) : ImportDataUseCase {
         return ImportDataUseCaseImpl(
             repository
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
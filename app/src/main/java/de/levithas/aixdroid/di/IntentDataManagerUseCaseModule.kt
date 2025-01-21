package de.levithas.aixdroid.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.levithas.aixdroid.data.repository.ExternalIntentRepository
import de.levithas.aixdroid.domain.usecase.intentmanager.IntentDataUseCase
import de.levithas.aixdroid.domain.usecase.intentmanager.IntentDataUseCaseImpl

@Module
@InstallIn(SingletonComponent::class)
object IntentDataManagerUseCaseModule {

    @Provides
    fun providesIntentDataUseCase(
        repository: ExternalIntentRepository
    ) : IntentDataUseCase {
        return IntentDataUseCaseImpl(repository)
    }
}
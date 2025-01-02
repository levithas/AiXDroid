package de.levithas.aixdroid.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.levithas.aixdroid.data.repository.DataRepository
import de.levithas.aixdroid.data.repository.ExternalIntentRepository
import de.levithas.aixdroid.services.ExternalIntentService
import de.levithas.aixdroid.services.intentactions.InferenceExternalIntentAction
import de.levithas.aixdroid.services.intentactions.ReadDataExternalIntentAction
import de.levithas.aixdroid.services.intentactions.WriteDataExternalIntentAction

@Module
@InstallIn(SingletonComponent::class)
object ServiceAndActionModule {

    @Provides
    fun providesReadDataIntentAction(
        context: Context,
        dataRepository : DataRepository
        ) : ReadDataExternalIntentAction {
        return ReadDataExternalIntentAction(context, dataRepository)
    }

    @Provides
    fun providesWriteDataIntentAction(
        context: Context,
        dataRepository : DataRepository
    ) : WriteDataExternalIntentAction {
        return WriteDataExternalIntentAction(context, dataRepository)
    }

    @Provides
    fun providesInferenceIntentAction(
        context: Context,
        dataRepository : DataRepository
    ) : InferenceExternalIntentAction {
        return InferenceExternalIntentAction(context, dataRepository)
    }

    @Provides
    fun providesExternalIntentService(
        intentRepository: ExternalIntentRepository,
        readDataExternalIntentAction: ReadDataExternalIntentAction,
        writeDataExternalIntentAction: WriteDataExternalIntentAction,
        inferenceExternalIntentAction: InferenceExternalIntentAction
    ) : ExternalIntentService {
        return ExternalIntentService(
            intentRepository,
            readDataExternalIntentAction,
            writeDataExternalIntentAction,
            inferenceExternalIntentAction
        )
    }
}
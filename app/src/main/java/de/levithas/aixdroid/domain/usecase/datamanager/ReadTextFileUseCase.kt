package de.levithas.aixdroid.domain.usecase.datamanager

import android.content.Context
import android.net.Uri
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Inject

interface ReadTextFileUseCase {
    suspend operator fun invoke(context: Context, uri: Uri) : String?
}

class ReadTextFileUseCaseImpl : ReadTextFileUseCase {
    override suspend fun invoke(context: Context, uri: Uri) : String? {
        try {
            var text = ""
            val inputStream = context.contentResolver.openInputStream(uri)
            val r = BufferedReader(InputStreamReader(inputStream))
            r.lines().forEach { line ->
                text.plus(line + '\n')
            }
            return text
        }
        catch (e: IOException) {
            e.message?.let { error(it) }
        }

        return null
    }
}
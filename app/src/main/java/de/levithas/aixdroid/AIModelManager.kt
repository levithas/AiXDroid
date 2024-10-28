package de.levithas.aixdroid
import android.app.Application
import android.net.Uri
import org.tensorflow.lite.Interpreter
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.util.Objects

class AIModelManager : Application() {

    @Throws(IOException::class)
    private fun loadModel(modelPathUri: Uri): Interpreter {
        try {
            Objects.requireNonNull(contentResolver.openInputStream(modelPathUri)).use { stream ->
                val buffer = ByteArray(1024)
                val baos = ByteArrayOutputStream()
                while (stream?.read(buffer) != -1) {
                        baos.write(buffer)
                    }

                val byteBuffer = MappedByteBuffer.allocateDirect(baos.size())
                byteBuffer.put(baos.toByteArray())
                byteBuffer.order(ByteOrder.nativeOrder())
                return Interpreter(byteBuffer)
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}


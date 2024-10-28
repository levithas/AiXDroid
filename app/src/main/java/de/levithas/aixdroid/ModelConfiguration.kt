package de.levithas.aixdroid

import org.tensorflow.lite.schema.Metadata
import java.nio.file.Path

data class ModelConfiguration(
    val name: String,
    val path: Path,
    val meta: Metadata
    )


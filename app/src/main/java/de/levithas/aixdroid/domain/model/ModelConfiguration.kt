package de.levithas.aixdroid.domain.model

import org.tensorflow.lite.schema.Metadata
import java.nio.file.Path

data class ModelConfiguration(
    val id: Int,
    val name: String,
    val path: Path,
    val meta: Metadata
)


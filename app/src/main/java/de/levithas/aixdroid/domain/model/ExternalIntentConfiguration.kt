package de.levithas.aixdroid.domain.model

data class ExternalIntentConfiguration(
    val name: String,
    val packageName: String,

    var allowReadData: Boolean,
    var allowWriteData: Boolean,
    var allowInference: Boolean
)

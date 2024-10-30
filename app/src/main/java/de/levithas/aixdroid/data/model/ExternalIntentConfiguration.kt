package de.levithas.aixdroid.data.model

data class ExternalIntentConfiguration(
    val name: String,
    val packageName: String,
    val permission: List<String> // TODO: Hinzufügen eines Enum für verschiedene Berechtigungen
)

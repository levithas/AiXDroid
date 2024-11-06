package de.levithas.aixdroid.utils


object DisplayUtilities {
    fun shortenString(input: String, maxLength: Int) : String {
        if (input.length <= maxLength) {
            return input  // Wenn der String nicht länger als maxLength ist, gib ihn unverändert zurück
        }

        // Kürze den String auf die maxLength, aber achte darauf, dass das letzte Wort nicht abgeschnitten wird
        val truncated = input.take(maxLength)

        // Finde das letzte Leerzeichen im gekürzten String
        val lastSpaceIndex = truncated.lastIndexOf(" ")

        // Wenn kein Leerzeichen gefunden wurde, wird der gesamte String gekürzt
        return if (lastSpaceIndex != -1) {
            truncated.substring(0, lastSpaceIndex) + "..."  // Füge "..." hinzu
        } else {
            truncated + "..."  // Falls kein Leerzeichen gefunden wird (z.B. nur ein sehr langes Wort), kürze dennoch und füge "..." hinzu
        }
    }
}
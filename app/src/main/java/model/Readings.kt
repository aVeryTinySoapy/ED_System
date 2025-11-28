package model

data class Reading(
    val siteName: String,
    val timestamp: String,
    val dissolvedOxygen: String,
    val phLevel: String,
    val temperature: String
)

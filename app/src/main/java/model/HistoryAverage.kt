package com.example.ed_system.model

data class HistoryAverage(
    val parameterName: String,
    val value: String,
    val status: String // e.g. "Stable", "Warning", etc.
)

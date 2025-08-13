package com.example.ed_system.model

data class Device(
    val name: String,
    val deviceId: String,
    val isOnline: Boolean,
    val batteryLevel: Int,
    val signalStrength: Int,
    val lastReading: String
)

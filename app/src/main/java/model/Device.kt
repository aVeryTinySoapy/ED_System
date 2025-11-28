package com.example.ed_system.model

data class Device(
    val name: String,
    val deviceId: String,
    val channelId: String, // ThingSpeak Channel
    var isOnline: Boolean,
    var lastReading: String
)

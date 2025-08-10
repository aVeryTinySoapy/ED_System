package com.example.ed_system.model

data class Device(
    val name: String,
    val id: String,
    val isConnected: Boolean,
    val battery: Int,
    val signal: Int,
    val lastRead: String
)

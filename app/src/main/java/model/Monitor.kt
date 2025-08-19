package com.example.ed_system.model

data class Monitor(
    val siteName: String,
    val siteTag: String,
    val deviceId: String,
    val lastUpdated: String,
    val doValue: String,
    val doStatus: String,
    val phValue: String,
    val phStatus: String,
    val tempValue: String,
    val tempStatus: String
)

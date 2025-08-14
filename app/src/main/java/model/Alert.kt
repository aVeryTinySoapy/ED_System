package com.example.ed_system.model

data class Alert(
    val iconRes: Int,
    val title: String,
    val description: String,
    val value: String,
    val threshold: String,
    val status: String,
    val timeAgo: String
)

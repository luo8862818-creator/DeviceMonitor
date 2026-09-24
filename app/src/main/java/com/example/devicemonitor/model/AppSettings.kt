package com.example.devicemonitor.model

data class AppSettings(
    val serverAddress: String = "192.168.4.110",
    val serverPort: String = "8080",
    val autoRefresh: Boolean = false
)
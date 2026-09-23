package com.example.devicemonitor.model

data class Device(
    val mac: String,
    val roomId: String,
    val type: String,
    val online: Boolean,
    val battery: Int
)
package com.example.devicemonitor.network

import com.example.devicemonitor.model.Device
import retrofit2.http.GET

interface DeviceApi {

    @GET("api/devices")
    suspend fun getDevices(): List<Device>
}
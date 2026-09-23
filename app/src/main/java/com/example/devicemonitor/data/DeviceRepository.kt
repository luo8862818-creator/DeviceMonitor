package com.example.devicemonitor.data

import android.util.Log
import com.example.devicemonitor.model.Device
import com.example.devicemonitor.network.RetrofitClient

object DeviceRepository {

    suspend fun getDevices(): List<Device> {

        Log.d(
            "DeviceRepository",
            "① 准备请求设备接口"
        )

        val devices =
            RetrofitClient.deviceApi.getDevices()

        Log.d(
            "DeviceRepository",
            "② 请求成功，设备数量=${devices.size}"
        )

        return devices
    }
}
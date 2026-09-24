package com.example.devicemonitor.data


import android.content.Context
import android.util.Log
import com.example.devicemonitor.model.Device
import com.example.devicemonitor.network.RetrofitClient
import kotlinx.coroutines.flow.first

class DeviceRepository(
    context: Context
) {
    private val settingsRepository = SettingsRepository(context)

    suspend fun getDevices(): List<Device> {
        Log.d("DeviceRepository", "① 准备读取服务器设置")

        val settings = settingsRepository.settings.first()

        Log.d(
            "DeviceRepository",
            "② 读取设置成功 address=${settings.serverAddress} port=${settings.serverPort}"
        )

        val deviceApi = RetrofitClient.createDeviceApi(
            serverAddress = settings.serverAddress,
            serverPort = settings.serverPort
        )

        Log.d("DeviceRepository", "③ Retrofit 创建完成，准备请求设备接口")

        val devices = deviceApi.getDevices()

        Log.d(
            "DeviceRepository",
            "④ 请求成功，设备数量=${devices.size}"
        )

        return devices
    }
}
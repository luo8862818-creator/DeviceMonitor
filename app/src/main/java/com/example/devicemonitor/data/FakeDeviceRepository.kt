package com.example.devicemonitor.data

import android.util.Log
import com.example.devicemonitor.model.Device
import com.example.devicemonitor.network.RetrofitClient
import kotlinx.coroutines.delay

object FakeDeviceRepository {

    private var refreshCount = 0

    suspend fun getDevices(): List<Device> {

        Log.d("DeviceRepository", "① 准备请求服务器")

        refreshCount++

        val devices =
            RetrofitClient.deviceApi.getDevices()

        Log.d(
            "DeviceRepository",
            "② 请求成功，设备数量=${devices.size}"
        )

        return devices

        /*        return List(20) { index ->

                    Device(
                        mac = "AA:BB:CC:${index + 1}",
                        roomId = "${101 + index}",
                        type = "mattress",
                        online = (index + refreshCount) % 3 != 0,
                        battery = 100 - index * 3
                    )
                }*/
    }


    suspend fun refreshDevices(): List<Device> {
        delay(1000)
//        throw Exception("模拟服务器连接失败")
        return getDevices()
    }
}
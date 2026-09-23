package com.example.devicemonitor.data

import com.example.devicemonitor.model.Device
import kotlinx.coroutines.delay

object FakeDeviceRepository {

    private var refreshCount = 0

    fun getDevices(): List<Device> {

        refreshCount++

        return List(20) { index ->

            Device(
                mac = "AA:BB:CC:${index + 1}",
                roomId = "${101 + index}",
                type = "mattress",
                online = (index + refreshCount) % 3 != 0,
                battery = 100 - index * 3
            )
        }
    }



    suspend fun refreshDevices(): List<Device> {
        delay(1000)
//        throw Exception("模拟服务器连接失败")
        return getDevices()
    }
}
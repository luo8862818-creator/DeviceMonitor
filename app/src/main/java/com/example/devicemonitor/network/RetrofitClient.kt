package com.example.devicemonitor.network

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    fun createDeviceApi(
        serverAddress: String,
        serverPort: String
    ): DeviceApi {
        val baseUrl = "http://${serverAddress.trim()}:${serverPort.trim()}/"

        Log.d(
            "RetrofitClient",
            "① 创建 Retrofit，baseUrl=$baseUrl"
        )

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(DeviceApi::class.java)
    }
}
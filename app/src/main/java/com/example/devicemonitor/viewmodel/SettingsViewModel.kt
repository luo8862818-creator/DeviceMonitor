package com.example.devicemonitor.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.devicemonitor.data.SettingsRepository
import com.example.devicemonitor.model.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application)

    val settings: StateFlow<AppSettings> = repository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppSettings()
    )

    private val _message = MutableSharedFlow<String>()
    val message: SharedFlow<String> = _message.asSharedFlow()

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    init {
        Log.d("SettingsViewModel", "SettingsViewModel 创建")
    }

    fun saveSettings(
        serverAddress: String,
        serverPort: String,
        autoRefresh: Boolean
    ) {
        if (serverAddress.isBlank() || serverPort.isBlank()) {
            showMessage("服务器地址和端口不能为空")
            return
        }

        val newSettings = AppSettings(
            serverAddress = serverAddress.trim(),
            serverPort = serverPort.trim(),
            autoRefresh = autoRefresh
        )

        viewModelScope.launch {
            repository.saveSettings(newSettings)
            _message.emit("设置已保存")
        }
    }

    fun testConnection(
        serverAddress: String,
        serverPort: String
    ) {
        Log.d(
            "SettingsViewModel",
            "① testConnection address=$serverAddress port=$serverPort"
        )

        if (serverAddress.isBlank() || serverPort.isBlank()) {
            Log.e("SettingsViewModel", "② 服务器地址或端口为空")

            viewModelScope.launch {
                _message.emit("服务器地址和端口不能为空")
            }

            return
        }

        viewModelScope.launch {
            Log.d(
                "SettingsViewModel",
                "② 协程启动，当前线程=${Thread.currentThread().name}"
            )

            try {
                val result = withContext(Dispatchers.IO) {
                    Log.d(
                        "SettingsViewModel",
                        "③ 进入 IO 线程，当前线程=${Thread.currentThread().name}"
                    )

                    val url =
                        "http://${serverAddress.trim()}:${serverPort.trim()}/api/devices"

                    Log.d("SettingsViewModel", "④ 请求地址=$url")

                    val request = Request.Builder()
                        .url(url)
                        .get()
                        .build()

                    Log.d("SettingsViewModel", "⑤ 开始发送 HTTP 请求")

                    httpClient.newCall(request).execute().use { response ->
                        Log.d(
                            "SettingsViewModel",
                            "⑥ 收到响应 code=${response.code}"
                        )

                        if (response.isSuccessful) {
                            "连接成功"
                        } else {
                            "连接失败：HTTP ${response.code}"
                        }
                    }
                }

                Log.d("SettingsViewModel", "⑦ 测试结果=$result")

                _message.emit(result)
            } catch (e: Exception) {
                Log.e(
                    "SettingsViewModel",
                    "测试连接异常：${e.javaClass.name}",
                    e
                )

                _message.emit(
                    "连接失败：${e.javaClass.simpleName} ${e.message ?: ""}"
                )
            }
        }
    }

    private fun showMessage(message: String) {
        viewModelScope.launch {
            _message.emit(message)
        }
    }
}
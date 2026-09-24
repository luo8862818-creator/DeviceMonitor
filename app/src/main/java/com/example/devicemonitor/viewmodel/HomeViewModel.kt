package com.example.devicemonitor.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.devicemonitor.data.DeviceRepository
import com.example.devicemonitor.data.SettingsRepository
import com.example.devicemonitor.model.Device
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class HomeUiState(
    val devices: List<Device> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "HomeViewModel"
        private const val AUTO_REFRESH_INTERVAL = 10_000L
    }

    private val deviceRepository = DeviceRepository(application)
    private val settingsRepository = SettingsRepository(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var autoRefreshJob: Job? = null

    init {
        Log.d(TAG, "① HomeViewModel 创建")

        refresh()
        observeAutoRefresh()
    }

    fun refresh() {
        Log.d(TAG, "② refresh() 被调用")

        viewModelScope.launch {
            Log.d(TAG, "③ 开始请求设备数据")

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val newDevices = deviceRepository.getDevices()

                Log.d(
                    TAG,
                    "④ 请求成功，设备数量=${newDevices.size}"
                )

                _uiState.value = _uiState.value.copy(
                    devices = newDevices,
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "④ 请求失败：${e.javaClass.simpleName} ${e.message}",
                    e
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "请求设备失败"
                )
            }
        }
    }

    private fun observeAutoRefresh() {
        viewModelScope.launch {
            settingsRepository.settings.collectLatest { settings ->
                Log.d(
                    TAG,
                    "⑤ 设置发生变化 autoRefresh=${settings.autoRefresh}"
                )

                if (settings.autoRefresh) {
                    startAutoRefresh()
                } else {
                    stopAutoRefresh()
                }
            }
        }
    }

    private fun startAutoRefresh() {
        if (autoRefreshJob?.isActive == true) {
            Log.d(TAG, "⑥ 自动刷新已经运行，不重复启动")
            return
        }

        Log.d(TAG, "⑥ 启动自动刷新，每 10 秒刷新一次")

        autoRefreshJob = viewModelScope.launch {
            while (true) {
                delay(AUTO_REFRESH_INTERVAL)

                Log.d(TAG, "⑦ 自动刷新触发")

                refresh()
            }
        }
    }

    private fun stopAutoRefresh() {
        Log.d(TAG, "⑥ 停止自动刷新")

        autoRefreshJob?.cancel()
        autoRefreshJob = null
    }
}
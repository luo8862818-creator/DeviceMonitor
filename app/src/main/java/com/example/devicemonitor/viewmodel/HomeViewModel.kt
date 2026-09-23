package com.example.devicemonitor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devicemonitor.data.FakeDeviceRepository
import com.example.devicemonitor.model.Device
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import android.util.Log
import com.example.devicemonitor.data.DeviceRepository


data class HomeUiState(
    val devices: List<Device> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState()
    )

    val uiState: StateFlow<HomeUiState> = _uiState


    init {
        Log.d("HomeViewModel", "① ViewModel 创建，首次加载设备")
        refresh()
    }


    fun refresh() {

        Log.d("HomeViewModel", "开始刷新设备")

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                Log.d("HomeViewModel", "开始调用 Repository")
                val newDevices = DeviceRepository.getDevices()
                _uiState.value = _uiState.value.copy(
                    devices = newDevices,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "未知错误"
                )
            }

        }
    }


}
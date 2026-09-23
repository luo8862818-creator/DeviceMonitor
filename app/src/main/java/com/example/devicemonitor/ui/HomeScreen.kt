package com.example.devicemonitor.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.devicemonitor.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val devices = uiState.devices

    val onlineCount = devices.count { device ->
        device.online
    }

    val offlineCount = devices.size - onlineCount


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "设备监控系统", fontSize = 24.sp)
        Text(text = "设备总数：${devices.size}")
        Text(text = "在线设备：${onlineCount}")
        Text(text = "离线设备：${offlineCount}")


        Button(
            onClick = {
                viewModel.refresh()
            },
            enabled = !uiState.isLoading

        ) {
            Text(
                text = if (uiState.isLoading) {
                    "刷新中..."
                } else {
                    "刷新设备"
                }
            )
        }


        if (uiState.isLoading) {
            CircularProgressIndicator()
        }

        uiState.errorMessage?.let { message ->
            Text(
                text = message
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(devices) { device ->
                DeviceCard(
                    device = device
                )
            }
        }
    }

}

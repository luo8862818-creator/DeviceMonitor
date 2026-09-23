package com.example.devicemonitor.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.devicemonitor.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {

    Log.d("HomeScreen", "① HomeScreen 开始执行")

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Log.d(
        "HomeScreen",
        "② 读取 uiState：loading=${uiState.isLoading}, error=${uiState.errorMessage}"
    )

    val snackbarHostState = remember {
        Log.d("HomeScreen", "③ 创建 SnackbarHostState")
        SnackbarHostState()
    }

    val devices = uiState.devices
    val onlineCount = devices.count { device ->
        device.online
    }
    val offlineCount = devices.size - onlineCount


    Log.d(
        "HomeScreen",
        "④ 计算数据：total=${devices.size}, online=$onlineCount, offline=$offlineCount"
    )


    val averageBattery = if (devices.isNotEmpty()) {
        devices.map { it.battery }
            .average()
            .toInt()
    } else {
        0
    }


    LaunchedEffect(uiState.errorMessage) {
        Log.d(
            "HomeScreen",
            "⑦ LaunchedEffect 真正开始执行：error=${uiState.errorMessage}"
        )
        uiState.errorMessage?.let { message ->
            Log.d("HomeScreen", "⑧ 准备显示 Snackbar：$message")
            snackbarHostState.showSnackbar(
                message = message
            )
            Log.d("HomeScreen", "⑨ Snackbar 显示结束")
        }
    }

    Log.d("HomeScreen", "⑤ 准备执行 Scaffold")
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title={
                    Column{
                        Text(
                            text = "设备监控"
                        )
                        Text(
                            text = "查看设备实时运行状态",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },

        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { innerPadding ->

        Log.d("HomeScreen", "⑥ Scaffold content 开始执行")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {


            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "全部设备",
                        value = devices.size.toString(),
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "在线设备",
                        value = onlineCount.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "离线设备",
                        value = offlineCount.toString(),
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "平均电量",
                        value = "$averageBattery%",
                        modifier = Modifier.weight(1f)
                    )
                }
            }


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


}


@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

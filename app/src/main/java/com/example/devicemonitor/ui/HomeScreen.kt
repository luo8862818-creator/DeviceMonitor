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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.devicemonitor.viewmodel.HomeViewModel


/*ALL      → 全部
ONLINE   → 在线
OFFLINE  → 离线*/
enum class DeviceFilter {
    ALL,
    ONLINE,
    OFFLINE
}

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


    var selectedFilter by remember {
        mutableStateOf(DeviceFilter.ALL)
    }

    val filteredDevices = when (selectedFilter) {

        DeviceFilter.ALL -> {
            devices
        }

        DeviceFilter.ONLINE -> {
            devices.filter { it.online }
        }

        DeviceFilter.OFFLINE -> {
            devices.filter { !it.online }
        }
    }


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
                title = {
                    Column {
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

            Text(
                text = "设备",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                FilterChip(
                    selected = selectedFilter == DeviceFilter.ALL,
                    onClick = {
                        selectedFilter = DeviceFilter.ALL
                    },
                    label = {
                        Text("全部 ${devices.size}")
                    }
                )

                FilterChip(
                    selected = selectedFilter == DeviceFilter.ONLINE,
                    onClick = {
                        selectedFilter = DeviceFilter.ONLINE
                    },
                    label = {
                        Text("在线 $onlineCount")
                    }
                )

                FilterChip(
                    selected = selectedFilter == DeviceFilter.OFFLINE,
                    onClick = {
                        selectedFilter = DeviceFilter.OFFLINE
                    },
                    label = {
                        Text("离线 $offlineCount")
                    }
                )
            }


            PullToRefreshBox(
                isRefreshing = uiState.isLoading,
                onRefresh = {
                    viewModel.refresh()
                },
                modifier = Modifier.weight(1f)
            ) {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredDevices) { device ->
                        DeviceCard(
                            device = device
                        )
                    }
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

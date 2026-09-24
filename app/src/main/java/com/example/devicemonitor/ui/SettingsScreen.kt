package com.example.devicemonitor.ui


import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.devicemonitor.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel()
) {

    Log.d("SettingsScreen", "========== 进入设置页面 ==========")

    val settings by viewModel.settings.collectAsStateWithLifecycle()

    var serverAddress by remember {
        mutableStateOf("")
    }

    var serverPort by remember {
        mutableStateOf("")
    }

    var autoRefresh by remember {
        mutableStateOf(false)
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    Log.d("SettingsScreen", "设置的参数是啥 ${settings}")
    Log.d("SettingsScreen", "我设置了一些基本的参数，服务器IP=${serverAddress}, snackbarHostState=${snackbarHostState.toString()}")

    LaunchedEffect(settings) {
        serverAddress = settings.serverAddress
        serverPort = settings.serverPort
        autoRefresh = settings.autoRefresh
    }

    LaunchedEffect(Unit) {
        viewModel.message.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "设置",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            ServerSettingsCard(
                serverAddress = serverAddress,
                serverPort = serverPort,
                onServerAddressChange = {
                    serverAddress = it
                },
                onServerPortChange = {
                    serverPort = it
                },
                onTestConnection = {
                    viewModel.testConnection(
                        serverAddress = serverAddress,
                        serverPort = serverPort
                    )
                }
            )

            AutoRefreshCard(
                autoRefresh = autoRefresh,
                onAutoRefreshChange = {
                    autoRefresh = it
                }
            )

            Button(
                onClick = {
                    viewModel.saveSettings(
                        serverAddress = serverAddress,
                        serverPort = serverPort,
                        autoRefresh = autoRefresh
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("保存设置")
            }
        }
    }
}

@Composable
private fun ServerSettingsCard(
    serverAddress: String,
    serverPort: String,
    onServerAddressChange: (String) -> Unit,
    onServerPortChange: (String) -> Unit,
    onTestConnection: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "服务器设置",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "配置设备数据接口的服务器地址",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = serverAddress,
                onValueChange = onServerAddressChange,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("服务器地址")
                },
                placeholder = {
                    Text("例如：192.168.1.100")
                },
                singleLine = true
            )

            OutlinedTextField(
                value = serverPort,
                onValueChange = { value ->
                    if (value.all { it.isDigit() }) {
                        onServerPortChange(value)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("服务器端口")
                },
                placeholder = {
                    Text("例如：8080")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            Button(
                onClick = onTestConnection,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("测试连接")
            }
        }
    }
}

@Composable
private fun AutoRefreshCard(
    autoRefresh: Boolean,
    onAutoRefreshChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "自动刷新",
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "定时获取最新设备状态",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = autoRefresh,
                onCheckedChange = onAutoRefreshChange
            )
        }
    }
}
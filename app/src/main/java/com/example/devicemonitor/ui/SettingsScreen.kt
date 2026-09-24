package com.example.devicemonitor.ui



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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    var serverAddress by remember {
        mutableStateOf("10.0.2.2")
    }

    var serverPort by remember {
        mutableStateOf("8080")
    }

    var autoRefresh by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
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
                // 下一步实现
            }
        )

        AutoRefreshCard(
            autoRefresh = autoRefresh,
            onAutoRefreshChange = {
                autoRefresh = it
            }
        )
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
                onValueChange = onServerPortChange,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("服务器端口")
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
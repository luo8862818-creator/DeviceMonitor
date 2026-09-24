package com.example.devicemonitor.ui




import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.devicemonitor.viewmodel.HomeViewModel

@Composable
fun DeviceScreen(
    viewModel: HomeViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var searchText by remember {
        mutableStateOf("")
    }

    var selectedFilter by remember {
        mutableStateOf(DeviceFilter.ALL)
    }

    val filteredDevices = uiState.devices.filter { device ->
        val matchesSearch =
            searchText.isBlank() ||
                    device.mac.contains(searchText, ignoreCase = true) ||
                    device.roomId.contains(searchText, ignoreCase = true) ||
                    device.type.contains(searchText, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            DeviceFilter.ALL -> true
            DeviceFilter.ONLINE -> device.online
            DeviceFilter.OFFLINE -> !device.online
        }

        matchesSearch && matchesFilter
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "设备",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "管理和查看所有设备",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = searchText,
            onValueChange = { newText ->
                searchText = newText
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("搜索设备")
            },
            placeholder = {
                Text("MAC、房间号或设备类型")
            },
            singleLine = true
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
                    Text("全部")
                }
            )

            FilterChip(
                selected = selectedFilter == DeviceFilter.ONLINE,
                onClick = {
                    selectedFilter = DeviceFilter.ONLINE
                },
                label = {
                    Text("在线")
                }
            )

            FilterChip(
                selected = selectedFilter == DeviceFilter.OFFLINE,
                onClick = {
                    selectedFilter = DeviceFilter.OFFLINE
                },
                label = {
                    Text("离线")
                }
            )
        }

        Text(
            text = "共 ${filteredDevices.size} 台设备",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredDevices) { device ->
                DeviceCard(
                    device = device,
                    onClick = {
                        navController.navigate("deviceDetail/${device.mac}")
                    }
                )
            }
        }
    }
}
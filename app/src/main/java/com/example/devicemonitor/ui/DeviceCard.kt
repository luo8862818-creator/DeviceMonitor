package com.example.devicemonitor.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.devicemonitor.model.Device

@Composable
fun DeviceCard(device: Device) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "房间：${device.roomId}",
                fontSize = 18.sp
            )

            Text("MAC：${device.mac}")
            Text("类型：${device.type}")

            Text(
                text = if (device.online) {
                    "状态：在线"
                } else {
                    "状态：离线"
                }
            )

            Text("电量：${device.battery}%")
        }
    }
}
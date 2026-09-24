package com.example.devicemonitor.ui


import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.devicemonitor.viewmodel.HomeViewModel

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

@Composable
fun DeviceMonitorApp() {


    // 创建导航控制器
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = viewModel()


    // 底部导航栏的三个页面
    val bottomItems = listOf(
        BottomNavItem(
            title = "首页",
            route = "home",
            icon = Icons.Default.Home
        ),
        BottomNavItem(
            title = "设备",
            route = "devices",
            icon = Icons.Default.List
        ),
        BottomNavItem(
            title = "设置",
            route = "settings",
            icon = Icons.Default.Settings
        )
    )

    // 获取当前所在页面
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(
                                    navController.graph.findStartDestination().id
                                ) {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(text = item.title)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(viewModel = homeViewModel)
            }

            composable("devices") {
                DeviceScreen(viewModel = homeViewModel, navController = navController)
            }

            composable("settings") {
                SettingsScreen()
            }

            composable("deviceDetail/{mac}") { backStackEntry ->
                val mac = backStackEntry.arguments?.getString("mac") ?: ""

                DeviceDetailScreen(
                    mac = mac,
                    viewModel = homeViewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
package com.tunneld.ipdiali.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tunneld.ipdiali.shared.feature.dnsleak.ui.DnsLeakScreen
import com.tunneld.ipdiali.shared.feature.home.ui.HomeRoute

private enum class Tab(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    IpLogger("IP logger", Icons.Filled.Wifi, Icons.Outlined.Wifi),
    DnsLeak("DNS leak test", Icons.Filled.Dns, Icons.Outlined.Dns),
}

@Composable
internal fun MainScreen(onSettings: () -> Unit, onExportCsv: (String) -> Unit, onDashboard: () -> Unit = {}) {
    var selectedTab by remember { mutableStateOf(Tab.IpLogger) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
            ) {
                Tab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.label,
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            )
                        },
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                    )
                }
            }
        },
    ) { padding ->
        when (selectedTab) {
            Tab.IpLogger -> HomeRoute(
                onSettings = onSettings,
                onExportCsv = onExportCsv,
                onDashboard = onDashboard,
                modifier = Modifier.padding(padding),
            )
            Tab.DnsLeak -> DnsLeakScreen(
                modifier = Modifier.padding(padding),
            )
        }
    }
}

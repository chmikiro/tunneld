package com.tunneld.ipdiali.shared.feature.dashboard.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun DashboardContent(
    csvContent: String,
    modifier: Modifier = Modifier,
)

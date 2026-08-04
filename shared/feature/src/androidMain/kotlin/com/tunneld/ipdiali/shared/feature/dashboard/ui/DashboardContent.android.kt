package com.tunneld.ipdiali.shared.feature.dashboard.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tunneld.ipdiali.feature.dashboard.DashboardWebView

@Composable
actual fun DashboardContent(
    csvContent: String,
    modifier: Modifier,
) {
    DashboardWebView(csvContent = csvContent, modifier = modifier)
}

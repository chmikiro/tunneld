package com.tunneld.ipdiali.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tunneld.ipdiali.shared.feature.dashboard.ui.DashboardScreen

@Composable
fun DashboardRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DashboardScreen(onBack = onBack, modifier = modifier)
}
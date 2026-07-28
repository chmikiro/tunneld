package com.tunneld.ipdiali.shared.feature.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SettingsRoute(
    onBack: () -> Unit,
    onRunInBackground: () -> Unit,
    onNotifications: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsScreen(
        onBack = onBack,
        onRunInBackground = onRunInBackground,
        onNotifications = onNotifications,
        modifier = modifier,
    )
}

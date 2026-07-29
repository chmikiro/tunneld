package com.tunneld.ipdiali.shared.feature.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SettingsRoute(
    onBack: () -> Unit,
    onRunInBackground: () -> Unit,
    onNotifications: () -> Unit,
    onImportCsv: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsScreen(
        onBack = onBack,
        onRunInBackground = onRunInBackground,
        onNotifications = onNotifications,
        onImportCsv = onImportCsv,
        modifier = modifier,
    )
}

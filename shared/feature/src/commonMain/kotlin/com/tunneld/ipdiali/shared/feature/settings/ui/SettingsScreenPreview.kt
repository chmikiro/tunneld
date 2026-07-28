package com.tunneld.ipdiali.shared.feature.settings.ui

import androidx.compose.runtime.Composable
import com.tunneld.ipdiali.shared.core.feature.ui.FindMyIpTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun SettingsScreenPreview() {
    FindMyIpTheme {
        SettingsScreen(
            onBack = {},
            onRunInBackground = {},
            onNotifications = {},
        )
    }
}

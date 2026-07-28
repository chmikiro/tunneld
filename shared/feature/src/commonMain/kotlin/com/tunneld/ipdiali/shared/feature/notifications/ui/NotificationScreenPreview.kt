package com.tunneld.ipdiali.shared.feature.notifications.ui

import androidx.compose.runtime.Composable
import com.tunneld.ipdiali.shared.core.feature.ui.FindMyIpTheme
import com.tunneld.ipdiali.shared.feature.notifications.presentation.NotificationSettingsUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun NotificationScreenPreview() {
    FindMyIpTheme {
        NotificationScreenImpl(
            onBack = {},
            uiState =
                NotificationSettingsUiState.Enabled(
                    wifiEnabled = true,
                    cellularEnabled = false,
                    vpnEnabled = false,
                    ipv4Enabled = true,
                    ipv6Enabled = true,
                ),
            onIntent = {},
            onSystemSettings = null,
        )
    }
}

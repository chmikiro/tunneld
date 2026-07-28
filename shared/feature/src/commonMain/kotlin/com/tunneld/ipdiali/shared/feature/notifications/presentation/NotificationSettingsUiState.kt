package com.tunneld.ipdiali.shared.feature.notifications.presentation

import androidx.compose.runtime.Immutable
import com.tunneld.ipdiali.shared.feature.notifications.presentation.NotificationSettingsUiState.Enabled
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@Immutable
sealed interface NotificationSettingsUiState {
    @Immutable data object Disabled : NotificationSettingsUiState

    @Immutable
    data class Enabled(
        val wifiEnabled: Boolean,
        val cellularEnabled: Boolean,
        val vpnEnabled: Boolean,

        val ipv4Enabled: Boolean,
        val ipv6Enabled: Boolean,
    ) : NotificationSettingsUiState
}

@OptIn(ExperimentalContracts::class)
fun NotificationSettingsUiState.isEnabled(): Boolean {
    contract { returns(true) implies (this@isEnabled is Enabled) }

    return this is Enabled
}

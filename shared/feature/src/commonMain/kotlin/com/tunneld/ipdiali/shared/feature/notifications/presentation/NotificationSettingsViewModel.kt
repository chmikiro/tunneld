package com.tunneld.ipdiali.shared.feature.notifications.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunneld.ipdiali.shared.core.application.usecase.ObserveNotificationPreferencesUseCase
import com.tunneld.ipdiali.shared.core.application.usecase.PartialUpdateNotificationPreferencesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationSettingsViewModel(
    private val observe: ObserveNotificationPreferencesUseCase,
    private val update: PartialUpdateNotificationPreferencesUseCase,
) : ViewModel() {

    val uiState =
        observe
            .observe()
            .map {
                if (it.enabled) {
                    NotificationSettingsUiState.Enabled(
                        wifiEnabled = it.wifi,
                        cellularEnabled = it.cellular,
                        vpnEnabled = it.vpn,

                        ipv4Enabled = it.ip4,
                        ipv6Enabled = it.ip6,
                    )
                } else {
                    NotificationSettingsUiState.Disabled
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )

    fun onIntent(intent: NotificationSettingsIntent) {
        viewModelScope.launch { suspendOnIntent(intent) }
    }

    private suspend fun suspendOnIntent(intent: NotificationSettingsIntent) {
        when (intent) {
            is NotificationSettingsIntent.ToggleCellular ->
                update.update(cellular = intent.newState)
            is NotificationSettingsIntent.ToggleIpv4 -> update.update(ip4 = intent.newState)
            is NotificationSettingsIntent.ToggleIpv6 -> update.update(ip6 = intent.newState)
            is NotificationSettingsIntent.ToggleNotifications ->
                update.update(enabled = intent.newState)
            is NotificationSettingsIntent.ToggleVpn -> update.update(vpn = intent.newState)
            is NotificationSettingsIntent.ToggleWifi -> update.update(wifi = intent.newState)
        }
    }
}

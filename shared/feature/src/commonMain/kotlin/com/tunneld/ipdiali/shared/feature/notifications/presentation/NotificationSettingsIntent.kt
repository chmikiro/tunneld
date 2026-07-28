package com.tunneld.ipdiali.shared.feature.notifications.presentation

sealed interface NotificationSettingsIntent {
    val newState: Boolean

    data class ToggleNotifications(override val newState: Boolean) : NotificationSettingsIntent

    data class ToggleWifi(override val newState: Boolean) : NotificationSettingsIntent

    data class ToggleCellular(override val newState: Boolean) : NotificationSettingsIntent

    data class ToggleVpn(override val newState: Boolean) : NotificationSettingsIntent

    data class ToggleIpv4(override val newState: Boolean) : NotificationSettingsIntent

    data class ToggleIpv6(override val newState: Boolean) : NotificationSettingsIntent

}

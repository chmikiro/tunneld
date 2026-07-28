package com.tunneld.ipdiali.shared.core.application.eventhandler

import com.tunneld.ipdiali.shared.core.application.event.EventHandler
import com.tunneld.ipdiali.shared.core.application.infrastructure.log.Logger
import com.tunneld.ipdiali.shared.core.application.infrastructure.notification.Notification
import com.tunneld.ipdiali.shared.core.application.infrastructure.notification.NotificationService
import com.tunneld.ipdiali.shared.core.application.infrastructure.preferences.UserPreferencesDataSource
import com.tunneld.ipdiali.shared.core.application.infrastructure.preferences.get
import com.tunneld.ipdiali.shared.core.domain.AddressHistory
import com.tunneld.ipdiali.shared.core.domain.NetworkType
import com.tunneld.ipdiali.shared.core.domain.NotificationPreferences
import com.tunneld.ipdiali.shared.core.domain.event.IpAddressChangedEvent
import tunneld.composeapp.generated.resources.*
import org.jetbrains.compose.resources.getString

internal class IpAddressChangeHandler(
    private val notificationService: NotificationService,
    private val notificationPreferences: UserPreferencesDataSource<NotificationPreferences>,
    private val logger: Logger,
) : EventHandler<IpAddressChangedEvent> {
    override suspend fun handle(event: IpAddressChangedEvent) {
        val prefs = notificationPreferences.get()

        if (!shouldNotify(prefs, event.newAddress)) {
            logger.d(TAG) { "Not notifying" }
            return
        }

        val notification =
            Notification.Info(
                id = event.newAddress.id.toInt(),
                topic = Notification.Topic.AddressChanged,
                title = getString(Res.string.headline_new_address_detected),
                message =
                    buildString {
                        append(event.newAddress.stringRepresentation())
                        append(", ")
                        append(event.newAddress.networkType.getString())
                    },
            )

        notificationService.notify(notification)
    }

    private suspend fun NetworkType.getString(): String =
        when (this) {
            NetworkType.Cellular -> getString(Res.string.cellular)
            NetworkType.Unknown -> getString(Res.string.unknown)
            NetworkType.VPN -> getString(Res.string.vpn)
            NetworkType.WiFi -> getString(Res.string.wifi)
        }

    private companion object {
        const val TAG = "IpAddressChangeHandler"
    }
}

private fun shouldNotify(prefs: NotificationPreferences, address: AddressHistory): Boolean {
    if (!prefs.enabled) {
        return false
    }

    val networkType =
        when {
            address.isWiFi && prefs.wifi -> true
            address.isCellular && prefs.cellular -> true
            address.isVPN && prefs.vpn -> true
            address.isUnknown && prefs.unknown -> true
            else -> false
        }

    val internetProtocol =
        when {
            address.isIPv4 && prefs.ip4 -> true
            address.isIPv6 && prefs.ip6 -> true
            else -> false
        }

    return networkType && internetProtocol
}

private val AddressHistory.isIPv4: Boolean
    get() = this is AddressHistory.Ipv4

private val AddressHistory.isIPv6: Boolean
    get() = this is AddressHistory.Ipv6

private val AddressHistory.isWiFi: Boolean
    get() = networkType == NetworkType.WiFi

private val AddressHistory.isCellular: Boolean
    get() = networkType == NetworkType.Cellular

private val AddressHistory.isUnknown: Boolean
    get() = networkType == NetworkType.Unknown

private val AddressHistory.isVPN: Boolean
    get() = networkType == NetworkType.VPN

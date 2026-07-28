package com.tunneld.ipdiali.shared.core.domain

data class NotificationPreferences(
    val enabled: Boolean,
    val wifi: Boolean,
    val cellular: Boolean,
    val vpn: Boolean,
    val unknown: Boolean,
    val ip4: Boolean,
    val ip6: Boolean,
)

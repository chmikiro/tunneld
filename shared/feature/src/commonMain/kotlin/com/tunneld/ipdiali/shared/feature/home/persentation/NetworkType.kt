package com.tunneld.ipdiali.shared.feature.home.persentation

import com.tunneld.ipdiali.shared.core.domain.NetworkType as DomainNetworkType

internal enum class NetworkType {
    UNKNOWN,
    WIFI,
    CELLULAR,
    VPN;

    companion object {
        fun fromDomain(domain: DomainNetworkType): NetworkType =
            when (domain) {
                DomainNetworkType.Unknown -> UNKNOWN
                DomainNetworkType.WiFi -> WIFI
                DomainNetworkType.Cellular -> CELLULAR
                DomainNetworkType.VPN -> VPN
            }
    }
}

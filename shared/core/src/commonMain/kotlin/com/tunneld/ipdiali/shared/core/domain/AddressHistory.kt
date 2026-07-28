package com.tunneld.ipdiali.shared.core.domain

import kotlinx.datetime.LocalDateTime

sealed interface AddressHistory : IpAddressString {
    val id: Long
    val domain: String?
    val dateTime: LocalDateTime
    val networkType: NetworkType

    fun copyWithId(newId: Long): AddressHistory =
        when (this) {
            is Ipv4 -> copy(id = newId)
            is Ipv6 -> copy(id = newId)
        }

    data class Ipv4(
        override val id: Long,
        override val domain: String?,
        override val dateTime: LocalDateTime,
        override val networkType: NetworkType,
        val address: Ip4Address,
        val ipInfo: IpInfo? = null,
    ) : AddressHistory {
        override fun stringRepresentation(): String = address.stringRepresentation()
    }

    data class Ipv6(
        override val id: Long,
        override val domain: String?,
        override val dateTime: LocalDateTime,
        override val networkType: NetworkType,
        val address: Ip6Address,
        val ipInfo: IpInfo? = null,
    ) : AddressHistory {
        override fun stringRepresentation(): String = address.stringRepresentation()
    }
}

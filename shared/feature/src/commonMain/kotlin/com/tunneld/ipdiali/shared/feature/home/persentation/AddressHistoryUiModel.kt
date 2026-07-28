package com.tunneld.ipdiali.shared.feature.home.persentation

import androidx.compose.runtime.Immutable
import com.tunneld.ipdiali.shared.core.domain.AddressHistory
import com.tunneld.ipdiali.shared.core.domain.IpInfo
import kotlinx.datetime.LocalDateTime

@Immutable
internal data class AddressHistoryUiModel(
    val id: Long,
    override val internetProtocolVersion: InternetProtocolVersion,
    override val address: String,
    override val domain: String?,
    override val dateTime: LocalDateTime,
    override val networkType: NetworkType,
    override val ipInfo: IpInfo? = null,
) : AddressUiModel {
    constructor(
        domain: AddressHistory
    ) : this(
        id = domain.id,
        internetProtocolVersion =
            when (domain) {
                is AddressHistory.Ipv4 -> InternetProtocolVersion.IPV4
                is AddressHistory.Ipv6 -> InternetProtocolVersion.IPV6
            },
        address = domain.stringRepresentation(),
        domain = domain.domain,
        dateTime = domain.dateTime,
        networkType = NetworkType.fromDomain(domain.networkType),
        ipInfo = when (domain) {
            is AddressHistory.Ipv4 -> domain.ipInfo
            is AddressHistory.Ipv6 -> domain.ipInfo
        },
    )
}

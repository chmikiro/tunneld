package com.tunneld.ipdiali.shared.feature.home.persentation

import com.tunneld.ipdiali.shared.core.domain.AddressStatus
import com.tunneld.ipdiali.shared.core.domain.Ip4Address
import com.tunneld.ipdiali.shared.core.domain.Ip6Address
import com.tunneld.ipdiali.shared.core.domain.IpInfo
import kotlin.jvm.JvmName
import kotlinx.datetime.LocalDateTime

internal sealed interface CurrentAddressUiModel {
    data object Unavailable : CurrentAddressUiModel

    data class Address(
        override val address: String,
        override val domain: String?,
        override val dateTime: LocalDateTime,
        override val internetProtocolVersion: InternetProtocolVersion,
        override val networkType: NetworkType,
        override val ipInfo: IpInfo? = null,
    ) : CurrentAddressUiModel, AddressUiModel

    companion object {
        @JvmName("fromIp4")
        fun from(addressStatus: AddressStatus<Ip4Address>) =
            when (addressStatus) {
                is AddressStatus.Error.Custom<*>,
                is AddressStatus.Error.Unknown<*> -> Unavailable

                is AddressStatus.Success<Ip4Address> ->
                    Address(
                        address = addressStatus.address.stringRepresentation(),
                        domain = addressStatus.domain,
                        dateTime = addressStatus.dateTime,
                        internetProtocolVersion = InternetProtocolVersion.IPV4,
                        networkType = NetworkType.fromDomain(addressStatus.networkType),
                        ipInfo = addressStatus.ipInfo,
                    )
            }

        @JvmName("fromIp6")
        fun from(addressStatus: AddressStatus<Ip6Address>) =
            when (addressStatus) {
                is AddressStatus.Error.Custom<*>,
                is AddressStatus.Error.Unknown<*> -> Unavailable

                is AddressStatus.Success<Ip6Address> ->
                    Address(
                        address = addressStatus.address.stringRepresentation(),
                        domain = addressStatus.domain,
                        dateTime = addressStatus.dateTime,
                        internetProtocolVersion = InternetProtocolVersion.IPV6,
                        networkType = NetworkType.fromDomain(addressStatus.networkType),
                        ipInfo = addressStatus.ipInfo,
                    )
            }
    }
}

internal fun CurrentAddressUiModel.isAvailable(): Boolean = this is CurrentAddressUiModel.Address

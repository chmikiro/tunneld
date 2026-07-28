package com.tunneld.ipdiali.shared.feature.home.persentation

import androidx.compose.runtime.Immutable
import com.tunneld.ipdiali.shared.core.domain.IpInfo
import kotlinx.datetime.LocalDateTime

@Immutable
internal interface AddressUiModel {
    val internetProtocolVersion: InternetProtocolVersion
    val address: String
    val domain: String?
    val dateTime: LocalDateTime
    val networkType: NetworkType
    val ipInfo: IpInfo?
}

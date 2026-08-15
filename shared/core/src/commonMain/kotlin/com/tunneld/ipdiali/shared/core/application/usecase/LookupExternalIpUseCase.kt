package com.tunneld.ipdiali.shared.core.application.usecase

import com.tunneld.ipdiali.shared.core.domain.IpInfo
import com.tunneld.ipdiali.shared.core.infrastructure.ipapi.GeoIpDataSource

fun interface LookupExternalIpUseCase {
    suspend fun lookup(ip: String): IpInfo?
}

internal class LookupExternalIpUseCaseImpl(
    private val geoIpDataSource: GeoIpDataSource,
) : LookupExternalIpUseCase {
    override suspend fun lookup(ip: String): IpInfo? =
        geoIpDataSource.getIpInfo(ip)
}

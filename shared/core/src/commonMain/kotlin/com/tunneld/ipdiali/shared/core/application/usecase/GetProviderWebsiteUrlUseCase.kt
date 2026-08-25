package com.tunneld.ipdiali.shared.core.application.usecase

import com.tunneld.ipdiali.shared.core.infrastructure.ipapi.GeoIpDataSource

fun interface GetProviderWebsiteUrlUseCase {
    suspend fun get(ip: String): String?
}

internal class GetProviderWebsiteUrlUseCaseImpl(
    private val geoIpDataSource: GeoIpDataSource,
) : GetProviderWebsiteUrlUseCase {
    override suspend fun get(ip: String): String? =
        geoIpDataSource.websiteUrl(ip)
}

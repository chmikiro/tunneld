package com.tunneld.ipdiali.shared.core.infrastructure.ipapi

import com.tunneld.ipdiali.shared.core.application.infrastructure.log.Logger
import com.tunneld.ipdiali.shared.core.domain.IpInfo

/**
 * Unified geolocation data source.
 * Reads provider selection + API key from preferences, delegates to the chosen provider.
 */
internal class GeoIpDataSource(
    private val providers: List<GeoIpProvider>,
    private val preferences: GeoIpPreferences,
    private val logger: Logger,
) {
    private val providerMap = providers.associateBy { it.id }

    /**
     * Look up geolocation for an IP using the user's chosen provider.
     * Returns null on any failure — enrichment is best-effort.
     */
    suspend fun getIpInfo(ip: String): IpInfo? {
        val selectedId = preferences.getSelectedProvider()
        val provider = providerMap[selectedId]
        if (provider == null) {
            logger.w(TAG) { "Unknown provider '$selectedId', falling back to first available" }
            return providers.firstOrNull()?.let { tryLookup(it, ip) }
        }
        return tryLookup(provider, ip)
    }

    private suspend fun tryLookup(provider: GeoIpProvider, ip: String): IpInfo? {
        val apiKey = if (provider.requiresApiKey) preferences.getApiKey(provider.id) else null
        if (provider.requiresApiKey && apiKey.isNullOrBlank()) {
            logger.w(TAG) { "Provider ${provider.id} requires API key but none configured" }
            return null
        }
        return try {
            provider.lookup(ip, apiKey)
        } catch (e: Exception) {
            logger.w(TAG, e) { "Provider ${provider.id} failed for $ip: ${e.message}" }
            null
        }
    }

    companion object {
        const val TAG = "GeoIpDataSource"
    }
}

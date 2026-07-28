package com.tunneld.ipdiali.shared.core.infrastructure.ipapi

import kotlinx.coroutines.flow.Flow

/**
 * Persisted preferences for geolocation provider selection and API keys.
 */
interface GeoIpPreferences {
    /** Currently selected provider ID (e.g. "ip2location", "ipapico", "ipinfo") */
    suspend fun getSelectedProvider(): String

    /** Observe the selected provider ID */
    fun observeSelectedProvider(): Flow<String>

    /** Set the selected provider */
    suspend fun setSelectedProvider(providerId: String)

    /** Get the API key for a given provider, or null if not set */
    suspend fun getApiKey(providerId: String): String?

    /** Set the API key for a given provider */
    suspend fun setApiKey(providerId: String, apiKey: String?)

    companion object {
        const val DEFAULT_PROVIDER = "ip2location"
    }
}

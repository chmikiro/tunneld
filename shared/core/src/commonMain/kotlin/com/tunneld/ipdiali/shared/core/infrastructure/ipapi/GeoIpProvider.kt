package com.tunneld.ipdiali.shared.core.infrastructure.ipapi

import com.tunneld.ipdiali.shared.core.domain.IpInfo

/**
 * A geolocation data provider that enriches an IP address with location/ISP info.
 * Providers are stateless — the lookup function handles everything.
 */
interface GeoIpProvider {
    /** Unique stable identifier, e.g. "ip2location", "ipapico", "ipinfo" */
    val id: String

    /** Human-readable name for the settings dropdown */
    val displayName: String

    /** Whether this provider requires an API key to function */
    val requiresApiKey: Boolean

    /** URL to the provider's signup / API key page */
    val signupUrl: String?

    /** Perform the lookup. apiKey may be null for free providers. */
    suspend fun lookup(ip: String, apiKey: String?): IpInfo?

    /** URL to view this IP on the provider's website. */
    fun websiteUrl(ip: String): String
}

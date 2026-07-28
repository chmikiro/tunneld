package com.tunneld.ipdiali.shared.core.domain

/**
 * Enriched IP geolocation / network information fetched from ip-api.com.
 * All fields are nullable — enrichment is best-effort, graceful degradation on failure.
 */
data class IpInfo(
    val country: String?,
    val countryCode: String?,
    val city: String?,
    val region: String?,
    val isp: String?,
    val org: String?,
    val asn: String?,
    val timezone: String?,
    val latitude: Float?,
    val longitude: Float?,
)

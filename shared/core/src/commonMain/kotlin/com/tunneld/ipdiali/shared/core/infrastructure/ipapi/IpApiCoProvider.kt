package com.tunneld.ipdiali.shared.core.infrastructure.ipapi

import com.tunneld.ipdiali.shared.core.domain.IpInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class IpApiCoProvider(
    private val httpClient: HttpClient,
) : GeoIpProvider {
    override val id = "ipapico"
    override val displayName = "ipapi.co"
    override val requiresApiKey = false
    override val signupUrl = null

    override suspend fun lookup(ip: String, apiKey: String?): IpInfo? {
        return try {
            val json: String = httpClient
                .get("https://ipapi.co/$ip/json/")
                .body()
            parseResponse(json)
        } catch (_: Exception) {
            null
        }
    }

    private fun parseResponse(json: String): IpInfo? {
        fun extract(key: String): String? {
            val match = Regex("\"$key\"\\s*:\\s*\"([^\"]*)\"").find(json)
            return match?.groupValues?.get(1)?.takeIf { it.isNotEmpty() }
        }
        // ipapi.co returns "error":true when rate-limited
        if (json.contains("\"error\":true")) return null
        val country = extract("country_name")
        val city = extract("city")
        val org = extract("org")
        if (country == null && city == null && org == null) return null
        return IpInfo(
            country = country,
            countryCode = extract("country_code"),
            city = city,
            region = extract("region"),
            isp = null,
            org = org,
            asn = null,
            timezone = extract("timezone"),
            latitude = extractFloatFromJson(json, "latitude"),
            longitude = extractFloatFromJson(json, "longitude"),
        )
    }
}

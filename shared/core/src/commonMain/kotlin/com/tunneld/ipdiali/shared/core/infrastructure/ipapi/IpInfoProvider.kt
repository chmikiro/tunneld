package com.tunneld.ipdiali.shared.core.infrastructure.ipapi

import com.tunneld.ipdiali.shared.core.domain.IpInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class IpInfoProvider(
    private val httpClient: HttpClient,
) : GeoIpProvider {
    override val id = "ipinfo"
    override val displayName = "ipinfo.io"
    override val requiresApiKey = false
    override val signupUrl = "https://ipinfo.io/signup"

    override suspend fun lookup(ip: String, apiKey: String?): IpInfo? {
        return try {
            val url = if (apiKey != null) {
                "https://ipinfo.io/$ip/json?token=$apiKey"
            } else {
                "https://ipinfo.io/$ip/json"
            }
            val json: String = httpClient.get(url).body()
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
        val city = extract("city")
        val region = extract("region")
        val country = extract("country") // ISO code
        val org = extract("org") // "AS15169 Google LLC"
        if (city == null && region == null && country == null && org == null) return null
        // Parse "loc": "37.4056,-122.0775"
        val loc = extract("loc")
        val lat = loc?.split(",")?.getOrNull(0)?.toFloatOrNull()
        val lon = loc?.split(",")?.getOrNull(1)?.toFloatOrNull()
        return IpInfo(
            country = null, // only ISO code available, no full name
            countryCode = country,
            city = city,
            region = region,
            isp = null,
            org = org,
            asn = null,
            timezone = extract("timezone"),
            latitude = lat,
            longitude = lon,
        )
    }
}

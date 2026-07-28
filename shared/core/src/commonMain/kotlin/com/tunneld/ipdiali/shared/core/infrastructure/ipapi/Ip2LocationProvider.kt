package com.tunneld.ipdiali.shared.core.infrastructure.ipapi

import com.tunneld.ipdiali.shared.core.domain.IpInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class Ip2LocationProvider(
    private val httpClient: HttpClient,
) : GeoIpProvider {
    override val id = "ip2location"
    override val displayName = "ip2location.io"
    override val requiresApiKey = true
    override val signupUrl = "https://www.ip2location.io/sign-up"

    override suspend fun lookup(ip: String, apiKey: String?): IpInfo? {
        val key = apiKey ?: return null
        return try {
            val json: String = httpClient
                .get("https://api.ip2location.io/?key=$key&ip=$ip")
                .body()
            parseResponse(json)
        } catch (_: Exception) {
            null
        }
    }

    private fun parseResponse(json: String): IpInfo? {
        fun extract(key: String): String? {
            val match = Regex("\"$key\"\\s*:\\s*\"([^\"]*)\"").find(json)
            return match?.groupValues?.get(1)?.takeIf { it != "-" && it.isNotEmpty() }
        }
        val country = extract("country_name")
        val city = extract("city_name")
        val isp = extract("isp")
        if (country == null && city == null && isp == null) return null
        return IpInfo(
            country = country,
            countryCode = extract("country_code"),
            city = city,
            region = extract("region_name"),
            isp = isp,
            org = extract("as"),
            asn = extract("asn"),
            timezone = extract("time_zone"),
            latitude = extractFloatFromJson(json, "latitude"),
            longitude = extractFloatFromJson(json, "longitude"),
        )
    }
}

package com.tunneld.ipdiali.shared.core.infrastructure.ipapi

import com.tunneld.ipdiali.shared.core.application.infrastructure.log.Logger
import com.tunneld.ipdiali.shared.core.domain.IpInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class IpApiDataSource(
    private val httpClient: HttpClient,
    private val logger: Logger,
    private val apiKey: String,
) {
    /**
     * Fetches geolocation / ISP information for the given IP via ip2location.io.
     * Returns null on any failure — enrichment is best-effort.
     */
    suspend fun getIpInfo(ip: String): IpInfo? {
        return try {
            val json: String = httpClient
                .get("https://api.ip2location.io/?key=$apiKey&ip=$ip")
                .body()
            parseResponse(json)
        } catch (e: Exception) {
            logger.w(TAG, e) { "Failed to fetch IP info for $ip: ${e.message}" }
            null
        }
    }

    /**
     * Lightweight JSON parsing — avoids adding kotlinx.serialization dependency.
     */
    private fun parseResponse(json: String): IpInfo? {
        fun extract(key: String): String? {
            val match = Regex("\"$key\"\\s*:\\s*\"([^\"]*)\"").find(json)
            return match?.groupValues?.get(1)?.takeIf { it != "-" && it.isNotEmpty() }
        }
        fun extractFloat(key: String): Float? {
            val match = Regex("\"$key\"\\s*:\\s*(-?[\\d.]+)").find(json)
            return match?.groupValues?.get(1)?.toFloatOrNull()
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
            latitude = extractFloat("latitude"),
            longitude = extractFloat("longitude"),
        )
    }

    private companion object {
        const val TAG = "IpApiDataSource"
    }
}

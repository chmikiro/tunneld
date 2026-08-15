package com.tunneld.ipdiali.shared.core.infrastructure.virustotal

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header

data class VtIpReport(
    val ip: String,
    val harmless: Int,
    val malicious: Int,
    val suspicious: Int,
    val undetected: Int,
) {
    val total: Int get() = harmless + malicious + suspicious + undetected
    val isClean: Boolean get() = malicious == 0 && suspicious == 0
}

interface VtIpLookup {
    suspend fun lookup(ip: String, apiKey: String): VtIpReport?
}

internal class KtorVtIpLookup(
    private val httpClient: HttpClient,
) : VtIpLookup {
    override suspend fun lookup(ip: String, apiKey: String): VtIpReport? {
        return try {
            val json: String = httpClient
                .get("https://www.virustotal.com/api/v3/ip_addresses/$ip") {
                    header("x-apikey", apiKey)
                }
                .body()
            parse(json, ip)
        } catch (_: Exception) {
            null
        }
    }

    private fun parse(json: String, ip: String): VtIpReport? {
        // VT returns {"error": ...} when no report exists or bad key
        if (json.contains("\"error\"")) return null
        fun stat(key: String): Int {
            val m = Regex("\"$key\"\\s*:\\s*(\\d+)").find(json) ?: return 0
            return m.groupValues[1].toIntOrNull() ?: 0
        }
        return VtIpReport(
            ip = ip,
            harmless = stat("harmless"),
            malicious = stat("malicious"),
            suspicious = stat("suspicious"),
            undetected = stat("undetected"),
        )
    }
}

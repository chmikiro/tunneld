package com.tunneld.ipdiali.shared.core.infrastructure.virustotal

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header

data class VtIpReport(
    val target: String,
    val harmless: Int,
    val malicious: Int,
    val suspicious: Int,
    val undetected: Int,
) {
    val total: Int get() = harmless + malicious + suspicious + undetected
    val isClean: Boolean get() = malicious == 0 && suspicious == 0
}

interface VtIpLookup {
    suspend fun lookupIp(ip: String, apiKey: String): VtIpReport?
    suspend fun lookupDomain(domain: String, apiKey: String): VtIpReport?
}

internal class KtorVtIpLookup(
    private val httpClient: HttpClient,
) : VtIpLookup {
    override suspend fun lookupIp(ip: String, apiKey: String): VtIpReport? =
        request("ip_addresses", ip, apiKey)

    override suspend fun lookupDomain(domain: String, apiKey: String): VtIpReport? =
        request("domains", domain, apiKey)

    private suspend fun request(kind: String, target: String, apiKey: String): VtIpReport? {
        return try {
            val json: String = httpClient
                .get("https://www.virustotal.com/api/v3/$kind/$target") {
                    header("x-apikey", apiKey)
                }
                .body()
            parse(json, target)
        } catch (_: Exception) {
            null
        }
    }

    private fun parse(json: String, target: String): VtIpReport? {
        // VT returns {"error": ...} when no report exists or bad key
        if (json.contains("\"error\"")) return null
        fun stat(key: String): Int {
            val m = Regex("\"$key\"\\s*:\\s*(\\d+)").find(json) ?: return 0
            return m.groupValues[1].toIntOrNull() ?: 0
        }
        return VtIpReport(
            target = target,
            harmless = stat("harmless"),
            malicious = stat("malicious"),
            suspicious = stat("suspicious"),
            undetected = stat("undetected"),
        )
    }
}

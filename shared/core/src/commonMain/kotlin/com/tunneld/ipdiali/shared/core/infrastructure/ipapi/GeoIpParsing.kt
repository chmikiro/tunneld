package com.tunneld.ipdiali.shared.core.infrastructure.ipapi

/**
 * Shared regex helpers for provider JSON parsing.
 */
internal fun extractFloatFromJson(json: String, key: String): Float? {
    val match = Regex("\"$key\"\\s*:\\s*(-?[\\d.]+)").find(json)
    return match?.groupValues?.get(1)?.toFloatOrNull()
}

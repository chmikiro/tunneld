package com.tunneld.ipdiali.shared.core.infrastructure.ipapi

import kotlinx.coroutines.flow.Flow

interface VtApiKeyPreferences {
    fun observeVtApiKey(): Flow<String?>
    suspend fun getVtApiKey(): String?
    suspend fun setVtApiKey(key: String?)
}

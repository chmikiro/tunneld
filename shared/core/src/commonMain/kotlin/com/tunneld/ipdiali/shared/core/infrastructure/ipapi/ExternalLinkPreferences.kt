package com.tunneld.ipdiali.shared.core.infrastructure.ipapi

import kotlinx.coroutines.flow.Flow

interface ExternalLinkPreferences {
    fun observeSkipVtConfirmation(): Flow<Boolean>
    fun observeSkipProviderConfirmation(): Flow<Boolean>
    suspend fun getSkipVtConfirmation(): Boolean
    suspend fun getSkipProviderConfirmation(): Boolean
    suspend fun setSkipVtConfirmation(skip: Boolean)
    suspend fun setSkipProviderConfirmation(skip: Boolean)
}

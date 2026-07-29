package com.tunneld.ipdiali.shared.core.application.infrastructure.local

import androidx.paging.PagingData
import com.tunneld.ipdiali.shared.core.domain.AddressHistory
import com.tunneld.ipdiali.shared.core.domain.NetworkType
import kotlinx.coroutines.flow.Flow

interface AddressHistoryLocalDataSource {
    fun observeHistory(
        query: String?,
        ipv4: Boolean,
        ipv6: Boolean,
        country: String? = null,
        networkTypes: Set<NetworkType> = emptySet(),
    ): Flow<PagingData<AddressHistory>>

    suspend fun saveHistory(history: AddressHistory): Long

    suspend fun getLatestIp4Address(): AddressHistory?

    suspend fun getLatestIp6Address(): AddressHistory?

    suspend fun getExportList(
        query: String?,
        ipv4: Boolean,
        ipv6: Boolean,
        country: String? = null,
        networkTypes: Set<NetworkType> = emptySet(),
    ): List<AddressHistory>

    suspend fun clearAll()
}
package com.tunneld.ipdiali.shared.core.application.usecase

import com.tunneld.ipdiali.shared.core.application.infrastructure.local.AddressHistoryLocalDataSource
import com.tunneld.ipdiali.shared.core.domain.AddressHistory
import com.tunneld.ipdiali.shared.core.domain.NetworkType

interface ExportAddressHistoryUseCase {
    suspend fun export(
        query: String?,
        ipv4: Boolean,
        ipv6: Boolean,
        networkTypes: Set<NetworkType>,
    ): List<AddressHistory>
}

internal class ExportAddressHistoryUseCaseImpl(
    private val dataSource: AddressHistoryLocalDataSource,
) : ExportAddressHistoryUseCase {
    override suspend fun export(
        query: String?,
        ipv4: Boolean,
        ipv6: Boolean,
        networkTypes: Set<NetworkType>,
    ): List<AddressHistory> =
        dataSource.getExportList(
            query = query,
            ipv4 = ipv4,
            ipv6 = ipv6,
            networkTypes = networkTypes,
        )
}
package com.tunneld.ipdiali.shared.core.application.usecase

import com.tunneld.ipdiali.shared.core.application.infrastructure.local.AddressHistoryLocalDataSource

interface ClearHistoryUseCase {
    suspend fun clear()
}

internal class ClearHistoryUseCaseImpl(
    private val dataSource: AddressHistoryLocalDataSource,
) : ClearHistoryUseCase {
    override suspend fun clear() = dataSource.clearAll()
}

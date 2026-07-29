package com.tunneld.ipdiali.shared.core.application.usecase

import com.tunneld.ipdiali.shared.core.application.infrastructure.local.AddressHistoryLocalDataSource

interface ImportCsvUseCase {
    suspend fun import(csvContent: String)
}

internal class ImportCsvUseCaseImpl(
    private val dataSource: AddressHistoryLocalDataSource,
) : ImportCsvUseCase {
    override suspend fun import(csvContent: String) = dataSource.importCsv(csvContent)
}

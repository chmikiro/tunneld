package com.tunneld.ipdiali.shared.core.application.infrastructure.transaction

interface TransactionScope<T> {
    suspend fun rollback(result: T)
}

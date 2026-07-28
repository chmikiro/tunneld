package com.tunneld.ipdiali.shared.core.infrastructure.inmemory

import com.tunneld.ipdiali.shared.core.application.infrastructure.local.CurrentAddressLocalDataSource
import com.tunneld.ipdiali.shared.core.domain.AddressStatus
import com.tunneld.ipdiali.shared.core.domain.IpAddress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

internal class InMemoryIpAddressDataSource<A : IpAddress> : CurrentAddressLocalDataSource<A> {
    private val ip = MutableStateFlow<AddressStatus<A>?>(null)

    override fun observe(): Flow<AddressStatus<A>> = ip.filterNotNull()

    override suspend fun update(status: AddressStatus<A>) {
        ip.value = status
    }
}

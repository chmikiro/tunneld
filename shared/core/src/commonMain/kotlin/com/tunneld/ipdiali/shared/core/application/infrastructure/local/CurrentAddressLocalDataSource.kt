package com.tunneld.ipdiali.shared.core.application.infrastructure.local

import com.tunneld.ipdiali.shared.core.domain.AddressStatus
import com.tunneld.ipdiali.shared.core.domain.IpAddress
import kotlinx.coroutines.flow.Flow

interface CurrentAddressLocalDataSource<A : IpAddress> {
    fun observe(): Flow<AddressStatus<A>>

    suspend fun update(status: AddressStatus<A>)
}

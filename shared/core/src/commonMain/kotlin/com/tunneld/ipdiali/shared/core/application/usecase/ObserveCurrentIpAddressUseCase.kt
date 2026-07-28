package com.tunneld.ipdiali.shared.core.application.usecase

import com.tunneld.ipdiali.shared.core.application.infrastructure.local.CurrentAddressLocalDataSource
import com.tunneld.ipdiali.shared.core.domain.AddressStatus
import com.tunneld.ipdiali.shared.core.domain.IpAddress
import kotlinx.coroutines.flow.Flow

fun interface ObserveCurrentIpAddressUseCase<A : IpAddress> {
    fun observe(): Flow<AddressStatus<A>>
}

internal class ObserveCurrentIpAddressUseCaseImpl<A : IpAddress>(
    private val currentAddressLocalDataSource: CurrentAddressLocalDataSource<A>
) : ObserveCurrentIpAddressUseCase<A> {
    override fun observe(): Flow<AddressStatus<A>> = currentAddressLocalDataSource.observe()
}

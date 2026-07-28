package com.tunneld.ipdiali.shared.core.application.infrastructure.remote

import com.tunneld.ipdiali.shared.core.domain.IpAddress

fun interface IpAddressRemoteDataSource<A : IpAddress> {
    suspend fun getCurrentIpAddress(): A
}

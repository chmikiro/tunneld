package com.tunneld.ipdiali.shared.core.infrastructure.fake

import com.tunneld.ipdiali.shared.core.application.infrastructure.remote.IpAddressRemoteDataSource
import com.tunneld.ipdiali.shared.core.domain.Ip4Address
import com.tunneld.ipdiali.shared.core.domain.Ip6Address
import com.tunneld.ipdiali.shared.core.infrastructure.mapper.StringToAddressMapper
import kotlin.random.Random

internal class FakeAddressDataSource(
    private val random: Random,
    private val stringToAddressMapper: StringToAddressMapper,
) {
    fun getCurrentIp4Address(): Ip4Address {
        // 20% for failure
        if (random.nextInt(5) == 0) {
            error("Fake failure")
        }

        return CommonAddresses.v4.random(random).let(stringToAddressMapper::toIp4Address)
    }

    fun getCurrentIp6Address(): Ip6Address {
        // 20% for failure
        if (random.nextInt(5) == 0) {
            error("Fake failure")
        }

        return CommonAddresses.v6.random(random).let(stringToAddressMapper::toIp6Address)
    }

    fun ip4Wrapper() = IpAddressRemoteDataSource { getCurrentIp4Address() }

    fun ip6Wrapper() = IpAddressRemoteDataSource { getCurrentIp6Address() }
}

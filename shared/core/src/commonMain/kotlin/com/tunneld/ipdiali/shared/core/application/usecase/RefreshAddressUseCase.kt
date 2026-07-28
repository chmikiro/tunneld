package com.tunneld.ipdiali.shared.core.application.usecase

import com.tunneld.ipdiali.shared.core.application.infrastructure.date.DateProvider
import com.tunneld.ipdiali.shared.core.application.infrastructure.dns.DnsService
import com.tunneld.ipdiali.shared.core.application.infrastructure.local.CurrentAddressLocalDataSource
import com.tunneld.ipdiali.shared.core.application.infrastructure.log.Logger
import com.tunneld.ipdiali.shared.core.application.infrastructure.network.NetworkTypeObserver
import com.tunneld.ipdiali.shared.core.application.infrastructure.remote.IpAddressRemoteDataSource
import com.tunneld.ipdiali.shared.core.application.infrastructure.result.Err
import com.tunneld.ipdiali.shared.core.application.infrastructure.result.Ok
import com.tunneld.ipdiali.shared.core.application.infrastructure.result.Result
import com.tunneld.ipdiali.shared.core.domain.AddressStatus
import com.tunneld.ipdiali.shared.core.domain.IpAddress
import com.tunneld.ipdiali.shared.core.domain.IpInfo

import com.tunneld.ipdiali.shared.core.infrastructure.ipapi.GeoIpDataSource

data class RefreshAddressError(val message: String?)

fun interface RefreshAddressUseCase {
    suspend fun refresh(): Result<Unit, RefreshAddressError>
}

internal class RefreshAddressUseCaseImpl<A : IpAddress>(
    private val saveAddressUseCase: SaveAddressHistoryUseCase,
    private val remoteDataSource: IpAddressRemoteDataSource<A>,
    private val current: CurrentAddressLocalDataSource<A>,
    private val dateProvider: DateProvider,
    private val dnsService: DnsService,
    private val networkTypeObserver: NetworkTypeObserver,
    private val geoIpDataSource: GeoIpDataSource,
    private val logger: Logger,
) : RefreshAddressUseCase {
    override suspend fun refresh(): Result<Unit, RefreshAddressError> {
        val now = dateProvider.now()

        return try {
            val networkType = networkTypeObserver.getNetworkType()
            val currentAddress = remoteDataSource.getCurrentIpAddress()
            val domain = dnsService.reverseLookup(currentAddress)
            val ipInfo = geoIpDataSource.getIpInfo(currentAddress.stringRepresentation())
            current.update(
                AddressStatus.Success(
                    dateTime = now,
                    address = currentAddress,
                    domain = domain,
                    networkType = networkType,
                    ipInfo = ipInfo,
                )
            )
            saveAddressUseCase.save(
                address = currentAddress,
                domain = domain,
                networkType = networkType,
                dateTime = now,
                ipInfo = ipInfo,
            )
            Ok(Unit)
        } catch (e: Exception) {
            logger.e(TAG, e) { "Failed to refresh current IP address" }

            when (val message = e.message) {
                null -> AddressStatus.Error.Unknown<A>(now)
                else -> AddressStatus.Error.Custom(now, message)
            }.let { current.update(it) }

            Err(RefreshAddressError(e.message))
        }
    }

    private companion object {
        const val TAG = "RefreshIp6AddressUseCaseImpl"
    }
}

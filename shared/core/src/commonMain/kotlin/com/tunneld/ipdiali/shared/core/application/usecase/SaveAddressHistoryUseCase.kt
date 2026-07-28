package com.tunneld.ipdiali.shared.core.application.usecase

import com.tunneld.ipdiali.shared.core.application.event.EventBus
import com.tunneld.ipdiali.shared.core.application.infrastructure.local.AddressHistoryLocalDataSource
import com.tunneld.ipdiali.shared.core.application.infrastructure.log.Logger
import com.tunneld.ipdiali.shared.core.application.infrastructure.transaction.TransactionProvider
import com.tunneld.ipdiali.shared.core.domain.AddressHistory
import com.tunneld.ipdiali.shared.core.domain.Ip4Address
import com.tunneld.ipdiali.shared.core.domain.Ip6Address
import com.tunneld.ipdiali.shared.core.domain.IpInfo
import com.tunneld.ipdiali.shared.core.domain.IpAddress
import com.tunneld.ipdiali.shared.core.domain.NetworkType
import com.tunneld.ipdiali.shared.core.domain.event.IpAddressChangedEvent
import kotlinx.datetime.LocalDateTime

interface SaveAddressHistoryUseCase {
    suspend fun <A : IpAddress> save(
        address: A,
        domain: String?,
        networkType: NetworkType,
        dateTime: LocalDateTime,
        ipInfo: IpInfo? = null,
    )
}

internal class SaveAddressHistoryUseCaseImpl(
    private val historyLocalDataSource: AddressHistoryLocalDataSource,
    private val transactionProvider: TransactionProvider,
    private val logger: Logger,
    private val eventBus: EventBus,
) : SaveAddressHistoryUseCase {
    override suspend fun <A : IpAddress> save(
        address: A,
        domain: String?,
        networkType: NetworkType,
        dateTime: LocalDateTime,
        ipInfo: IpInfo?,
    ) {
        transactionProvider.immediate {
            val latest = historyLocalDataSource.getLatestAddressOfType(address)

            if (
                latest != null &&
                    latest.stringRepresentation() == address.stringRepresentation() &&
                    latest.networkType == networkType
            ) {
                logger.d(TAG) { "Current IP address is the same as the latest one, skipping save." }
            } else {
                logger.d(TAG) { "Saving new current IP address" }
                val newAddressHistory = createHistory(address, domain, networkType, dateTime, ipInfo)
                val id = historyLocalDataSource.saveHistory(newAddressHistory)
                val newAddressHistoryWithId = newAddressHistory.copyWithId(id)
                eventBus.publish(IpAddressChangedEvent(newAddressHistoryWithId))
            }
        }
    }

    private suspend fun <A : IpAddress> AddressHistoryLocalDataSource.getLatestAddressOfType(
        address: A
    ): AddressHistory? =
        when (address) {
            is Ip4Address -> getLatestIp4Address()
            is Ip6Address -> getLatestIp6Address()
        }

    private fun <A : IpAddress> createHistory(
        address: A,
        domain: String?,
        networkType: NetworkType,
        dateTime: LocalDateTime,
        ipInfo: IpInfo?,
    ): AddressHistory =
        when (address) {
            is Ip4Address ->
                AddressHistory.Ipv4(
                    id = 0,
                    address = address,
                    domain = domain,
                    networkType = networkType,
                    dateTime = dateTime,
                    ipInfo = ipInfo,
                )

            is Ip6Address ->
                AddressHistory.Ipv6(
                    id = 0,
                    address = address,
                    domain = domain,
                    networkType = networkType,
                    dateTime = dateTime,
                    ipInfo = ipInfo,
                )
        }

    companion object {
        private const val TAG = "SaveAddressHistoryUseCase"
    }
}

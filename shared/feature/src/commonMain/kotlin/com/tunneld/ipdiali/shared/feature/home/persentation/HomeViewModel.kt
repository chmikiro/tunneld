package com.tunneld.ipdiali.shared.feature.home.persentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.tunneld.ipdiali.shared.core.application.usecase.ExportAddressHistoryUseCase
import com.tunneld.ipdiali.shared.core.application.usecase.ObserveAddressHistoryUseCase
import com.tunneld.ipdiali.shared.core.application.usecase.ObserveCurrentIpAddressUseCase
import com.tunneld.ipdiali.shared.core.application.usecase.RefreshAddressUseCase
import com.tunneld.ipdiali.shared.core.domain.AddressHistory
import com.tunneld.ipdiali.shared.core.domain.Ip4Address
import com.tunneld.ipdiali.shared.core.domain.Ip6Address
import com.tunneld.ipdiali.shared.core.domain.IpInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class HomeViewModel(
    observeHistoryUseCase: ObserveAddressHistoryUseCase,
    observeCurrentIp4AddressUseCase: ObserveCurrentIpAddressUseCase<Ip4Address>,
    observeCurrentIp6AddressUseCase: ObserveCurrentIpAddressUseCase<Ip6Address>,
    private val refreshIp4AddressUseCase: RefreshAddressUseCase,
    private val refreshIp6AddressUseCase: RefreshAddressUseCase,
    private val exportAddressHistoryUseCase: ExportAddressHistoryUseCase,
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    val ipv4 =
        observeCurrentIp4AddressUseCase
            .observe()
            .map(CurrentAddressUiModel::from)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = CurrentAddressUiModel.Unavailable,
            )

    val ipv6 =
        observeCurrentIp6AddressUseCase
            .observe()
            .map(CurrentAddressUiModel::from)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = CurrentAddressUiModel.Unavailable,
            )

    private val _filter = MutableStateFlow(Filter(setOf()))
    val filter = _filter.asStateFlow()

    fun updateFilter(newFilter: Filter) {
        _filter.value = newFilter
    }

    private val _searchQuery =
        MutableSharedFlow<String?>(replay = 1).apply { runBlocking { emit(null) } }
    val searchQuery = _searchQuery.asSharedFlow()

    fun search(query: String?) {
        viewModelScope.launch { _searchQuery.emit(query) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val history =
        combine(filter, searchQuery) { filter, query ->
                observeHistoryUseCase
                    .observe(
                        query = query,
                        ipv4 =
                            filter.protocols.contains(InternetProtocolVersion.IPV4) ||
                                filter.protocols.isEmpty(),
                        ipv6 =
                            filter.protocols.contains(InternetProtocolVersion.IPV6) ||
                                filter.protocols.isEmpty(),
                        country = filter.country,
                        networkTypes = filter.networkTypes,
                    )
                    .map { data -> data.map(::AddressHistoryUiModel) }
            }
            .flatMapLatest { it }
            .cachedIn(viewModelScope)

    init {
        viewModelScope.launch { refresh() }
    }

    fun refresh() {
        if (_isRefreshing.value) return

        _isRefreshing.value = true

        val ip4Job =
            viewModelScope.async {
                delay(REFRESH_DELAY_MS)
                refreshIp4AddressUseCase.refresh()
            }
        val ip6Job =
            viewModelScope.async {
                delay(REFRESH_DELAY_MS)
                refreshIp6AddressUseCase.refresh()
            }

        viewModelScope.launch {
            awaitAll(ip4Job, ip6Job)
            _isRefreshing.value = false
        }
    }

    suspend fun exportToCsv(): String {
        val f = _filter.value
        val results = exportAddressHistoryUseCase.export(
            query = null,
            ipv4 = f.protocols.contains(InternetProtocolVersion.IPV4) || f.protocols.isEmpty(),
            ipv6 = f.protocols.contains(InternetProtocolVersion.IPV6) || f.protocols.isEmpty(),
            country = f.country,
            networkTypes = f.networkTypes,
        )

        val sb = StringBuilder()
        sb.appendLine("address,version,network_type,country,country_code,city,isp,org,asn,timezone,latitude,longitude,timestamp")
        results.forEach { item ->
            val info = when (item) {
                is AddressHistory.Ipv4 -> item.ipInfo
                is AddressHistory.Ipv6 -> item.ipInfo
            }
            val version = when (item) {
                is AddressHistory.Ipv4 -> "IPv4"
                is AddressHistory.Ipv6 -> "IPv6"
            }
            val nt = item.networkType.toString().substringAfterLast(".")
            sb.appendLine(
                listOf(
                    item.stringRepresentation(),
                    version,
                    nt,
                    info?.country ?: "",
                    info?.countryCode ?: "",
                    info?.city ?: "",
                    info?.isp ?: "",
                    info?.org ?: "",
                    info?.asn ?: "",
                    info?.timezone ?: "",
                    info?.latitude?.toString() ?: "",
                    info?.longitude?.toString() ?: "",
                    item.dateTime.toString(),
                ).joinToString(",") { "\"${it.replace("\"", "\"\"")}\"" }
            )
        }
        return sb.toString()
    }

    companion object {
        private const val REFRESH_DELAY_MS = 1000L
    }
}
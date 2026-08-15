package com.tunneld.ipdiali.shared.feature.home.persentation

import androidx.compose.runtime.Immutable
import com.tunneld.ipdiali.shared.core.domain.NetworkType

@Immutable
internal data class Filter(
    val protocols: Set<InternetProtocolVersion>,
    val networkTypes: Set<NetworkType> = emptySet(),
    val query: String? = null,
) {
    val filtersCount =
        protocols.size + networkTypes.size + (if (query != null) 1 else 0)

    fun toggleInternetProtocol(protocol: InternetProtocolVersion): Filter =
        if (protocols.contains(protocol)) {
            copy(protocols = protocols - protocol)
        } else {
            copy(protocols = protocols + protocol)
        }

    fun toggleNetworkType(networkType: NetworkType): Filter =
        if (networkTypes.contains(networkType)) {
            copy(networkTypes = networkTypes - networkType)
        } else {
            copy(networkTypes = networkTypes + networkType)
        }

    fun setQuery(newQuery: String?): Filter = copy(query = newQuery)
}
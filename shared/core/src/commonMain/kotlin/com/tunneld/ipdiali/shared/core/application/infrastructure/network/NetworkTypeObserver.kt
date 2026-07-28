package com.tunneld.ipdiali.shared.core.application.infrastructure.network

import com.tunneld.ipdiali.shared.core.domain.NetworkType

interface NetworkTypeObserver {
    fun getNetworkType(): NetworkType
}

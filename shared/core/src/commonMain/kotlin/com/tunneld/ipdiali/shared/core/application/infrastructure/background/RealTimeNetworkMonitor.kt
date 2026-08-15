package com.tunneld.ipdiali.shared.core.application.infrastructure.background

import kotlinx.coroutines.flow.Flow

interface RealTimeNetworkMonitor {
    val isRunning: Flow<Boolean>

    suspend fun start()

    suspend fun stop()
}
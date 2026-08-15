package com.tunneld.ipdiali.feature.background.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunneld.ipdiali.shared.core.application.infrastructure.background.PeriodicWorkManager
import com.tunneld.ipdiali.shared.core.application.infrastructure.background.RealTimeNetworkMonitor
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class BackgroundWorkViewModel(
    private val periodicWorkManager: PeriodicWorkManager,
    private val realTimeMonitor: RealTimeNetworkMonitor,
) : ViewModel() {
    val periodicWorkerRunning =
        periodicWorkManager.isRunning.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = runBlocking { periodicWorkManager.isRunning.first() },
        )

    val realTimeMonitorRunning =
        realTimeMonitor.isRunning.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = runBlocking { realTimeMonitor.isRunning.first() },
        )

    fun togglePeriodicWorker(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) periodicWorkManager.start()
            else periodicWorkManager.stop()
        }
    }

    fun toggleRealTimeMonitor(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) realTimeMonitor.start()
            else realTimeMonitor.stop()
        }
    }
}
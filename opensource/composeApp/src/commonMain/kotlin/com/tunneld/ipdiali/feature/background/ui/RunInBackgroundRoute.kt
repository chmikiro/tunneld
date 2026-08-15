package com.tunneld.ipdiali.feature.background.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tunneld.ipdiali.feature.background.presentation.BackgroundWorkViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RunInBackgroundRoute(onBack: () -> Unit, modifier: Modifier = Modifier.Companion) {
    val viewModel: BackgroundWorkViewModel = koinViewModel()

    val periodicWorkerRunning by viewModel.periodicWorkerRunning.collectAsStateWithLifecycle()
    val realTimeMonitorRunning by viewModel.realTimeMonitorRunning.collectAsStateWithLifecycle()

    RunInBackgroundScreen(
        onBack = onBack,
        periodicRefreshRunning = periodicWorkerRunning,
        realTimeMonitorRunning = realTimeMonitorRunning,
        onTogglePeriodicRefresh = viewModel::togglePeriodicWorker,
        onToggleRealTimeMonitor = viewModel::toggleRealTimeMonitor,
        modifier = modifier,
    )
}
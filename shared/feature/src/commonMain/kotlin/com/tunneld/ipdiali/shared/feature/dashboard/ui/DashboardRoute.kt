package com.tunneld.ipdiali.shared.feature.dashboard.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tunneld.ipdiali.shared.core.application.usecase.ExportAddressHistoryUseCase
import org.koin.compose.koinInject

@Composable
fun DashboardRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val exportUseCase: ExportAddressHistoryUseCase = koinInject()
    DashboardScreen(
        onBack = onBack,
        exportUseCase = exportUseCase,
        modifier = modifier,
    )
}

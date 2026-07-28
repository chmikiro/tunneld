package com.tunneld.ipdiali.shared.feature.notifications.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tunneld.ipdiali.shared.feature.notifications.presentation.NotificationSettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NotificationsRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationSettingsViewModel = koinViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    if (uiState == null) {
        // TODO loading state
        return
    }

    NotificationScreen(
        onBack = onBack,
        uiState = uiState,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

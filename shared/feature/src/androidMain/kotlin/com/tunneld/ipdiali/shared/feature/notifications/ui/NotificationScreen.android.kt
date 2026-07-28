package com.tunneld.ipdiali.shared.feature.notifications.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.tunneld.ipdiali.shared.feature.notifications.presentation.NotificationSettingsIntent
import com.tunneld.ipdiali.shared.feature.notifications.presentation.NotificationSettingsUiState

@Composable
internal actual fun NotificationScreen(
    onBack: () -> Unit,
    uiState: NotificationSettingsUiState,
    onIntent: (NotificationSettingsIntent) -> Unit,
    modifier: Modifier,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Android33NotificationScreen(onBack, uiState, onIntent, modifier)
    } else {
        AndroidNotificationScreen(onBack, uiState, onIntent, modifier)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun Android33NotificationScreen(
    onBack: () -> Unit,
    uiState: NotificationSettingsUiState,
    onIntent: (NotificationSettingsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current
    var requestInSettings by remember { mutableStateOf(false) }
    val permissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            isGranted ->
            if (
                activity != null &&
                    !isGranted &&
                    !shouldShowRequestPermissionRationale(
                        activity,
                        Manifest.permission.POST_NOTIFICATIONS,
                    )
            ) {
                requestInSettings = true
            }

            if (isGranted) {
                onIntent(NotificationSettingsIntent.ToggleNotifications(true))
            }
        }

    val permissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    if (requestInSettings && !permissionState.status.isGranted) {
        AndroidNotificationsRedirectToSettingsAlertDialog(
            onDismissRequest = { requestInSettings = false },
            onConfirm = {
                activity?.let { redirectToNotificationsSettings(it) }
                requestInSettings = false
            },
        )
    }

    AndroidNotificationScreen(
        onBack = onBack,
        uiState = uiState.applyNotificationsPermission(),
        onIntent = {
            when (it) {
                is NotificationSettingsIntent.ToggleNotifications if
                    (it.newState && !permissionState.status.isGranted)
                 -> permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

                else -> onIntent(it)
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun AndroidNotificationScreen(
    onBack: () -> Unit,
    uiState: NotificationSettingsUiState,
    onIntent: (NotificationSettingsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    NotificationScreenImpl(
        onBack = onBack,
        uiState = uiState,
        onIntent = onIntent,
        onSystemSettings = { redirectToNotificationsSettings(context) },
        modifier = modifier,
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun NotificationSettingsUiState.applyNotificationsPermission():
    NotificationSettingsUiState =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val state = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

        remember(state.status, this) {
            if (state.status.isGranted) {
                this
            } else {
                NotificationSettingsUiState.Disabled
            }
        }
    } else {
        this
    }

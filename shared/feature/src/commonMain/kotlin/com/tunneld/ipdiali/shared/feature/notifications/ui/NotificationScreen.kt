package com.tunneld.ipdiali.shared.feature.notifications.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.horizontal
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.NetworkCell
import androidx.compose.material.icons.outlined.NetworkWifi
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.tunneld.ipdiali.shared.core.feature.ui.ArrowBackIconButton
import com.tunneld.ipdiali.shared.feature.notifications.presentation.NotificationSettingsIntent
import com.tunneld.ipdiali.shared.feature.notifications.presentation.NotificationSettingsUiState
import com.tunneld.ipdiali.shared.feature.notifications.presentation.isEnabled
import tunneld.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal expect fun NotificationScreen(
    onBack: () -> Unit,
    uiState: NotificationSettingsUiState,
    onIntent: (NotificationSettingsIntent) -> Unit,
    modifier: Modifier = Modifier,
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun NotificationScreenImpl(
    onBack: () -> Unit,
    uiState: NotificationSettingsUiState,
    onIntent: (NotificationSettingsIntent) -> Unit,
    onSystemSettings: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val density = LocalDensity.current

    var buttonHeight by remember { mutableStateOf(0) }

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_notifications)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        Box(Modifier.fillMaxSize()) {
            Box(
                Modifier.fillMaxWidth()
                    .padding(16.dp)
                    .padding(top = paddingValues.calculateTopPadding())
                    .padding(paddingValues.horizontal())
                    .zIndex(10f)
                    .onSizeChanged { buttonHeight = it.height }
            ) {
                HeadlineButton(
                    state = uiState.isEnabled(),
                    onStateChange = {
                        onIntent(NotificationSettingsIntent.ToggleNotifications(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            LazyColumn(
                modifier =
                    Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding =
                    paddingValues.add(top = with(density) { buttonHeight.toDp() } + 32.dp),
            ) {
                if (uiState.isEnabled()) {
                    item {
                        NetworkTypeSettings(
                            wifi = uiState.wifiEnabled,
                            onToggleWifi = { onIntent(NotificationSettingsIntent.ToggleWifi(it)) },
                            cellular = uiState.cellularEnabled,
                            onToggleCellular = {
                                onIntent(NotificationSettingsIntent.ToggleCellular(it))
                            },
                            vpn = uiState.vpnEnabled,
                            onToggleVpn = { onIntent(NotificationSettingsIntent.ToggleVpn(it)) },
                        )
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                    item {
                        InternetProtocolSettings(
                            ipv4 = uiState.ipv4Enabled,
                            onToggleIpv4 = { onIntent(NotificationSettingsIntent.ToggleIpv4(it)) },
                            ipv6 = uiState.ipv6Enabled,
                            onToggleIpv6 = { onIntent(NotificationSettingsIntent.ToggleIpv6(it)) },
                        )
                    }
                    if (onSystemSettings != null) {
                        item { HorizontalDivider() }
                        item {
                            ListItem(
                                headlineContent = {
                                    Text(
                                        stringResource(
                                            Res.string.headline_system_notifications_settings
                                        )
                                    )
                                },
                                modifier =
                                    Modifier.heightIn(min = 68.dp).clickable { onSystemSettings() },
                            )
                        }
                    }
                } else {
                    item {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(imageVector = Icons.Outlined.Info, contentDescription = null)

                            Text(
                                text =
                                    stringResource(
                                        Res.string.neutral_you_havent_allowed_notifications
                                    ),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HeadlineButton(
    state: Boolean,
    onStateChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = { onStateChange(!state) },
        shapes = ButtonDefaults.shapesFor(ButtonDefaults.LargeContainerHeight),
        modifier = modifier.fillMaxWidth().heightIn(min = ButtonDefaults.LargeContainerHeight),
        contentPadding = ButtonDefaults.LargeContentPadding,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(Res.string.description_notifications),
                style = MaterialTheme.typography.titleLargeEmphasized,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(ButtonDefaults.LargeIconSpacing))
            Switch(checked = state, onCheckedChange = null)
        }
    }
}

@Composable
private fun NetworkTypeSettings(
    wifi: Boolean,
    onToggleWifi: (Boolean) -> Unit,
    cellular: Boolean,
    onToggleCellular: (Boolean) -> Unit,
    vpn: Boolean,
    onToggleVpn: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.headline_network_type),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.description_network_type_notifications),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(Modifier.height(8.dp))

        ListItem(
            headlineContent = { Text(text = stringResource(Res.string.wifi)) },
            modifier = Modifier.heightIn(min = 68.dp).clickable { onToggleWifi(!wifi) },
            trailingContent = { Switch(checked = wifi, onCheckedChange = null) },
            leadingContent = {
                Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Outlined.NetworkWifi, contentDescription = null)
                }
            },
        )

        ListItem(
            headlineContent = { Text(text = stringResource(Res.string.cellular)) },
            modifier = Modifier.heightIn(min = 68.dp).clickable { onToggleCellular(!cellular) },
            trailingContent = { Switch(checked = cellular, onCheckedChange = null) },
            leadingContent = {
                Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Outlined.NetworkCell, contentDescription = null)
                }
            },
        )

        ListItem(
            headlineContent = { Text(text = stringResource(Res.string.vpn)) },
            modifier = Modifier.heightIn(min = 68.dp).clickable { onToggleVpn(!vpn) },
            trailingContent = { Switch(checked = vpn, onCheckedChange = null) },
            leadingContent = {
                Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Outlined.VpnKey, contentDescription = null)
                }
            },
        )


    }
}

@Composable
private fun InternetProtocolSettings(
    ipv4: Boolean,
    onToggleIpv4: (Boolean) -> Unit,
    ipv6: Boolean,
    onToggleIpv6: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.headline_internet_protocol),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.description_internet_protocol_notifications),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(Modifier.height(8.dp))

        ListItem(
            headlineContent = { Text(text = stringResource(Res.string.ipv4)) },
            modifier = Modifier.heightIn(min = 68.dp).clickable { onToggleIpv4(!ipv4) },
            trailingContent = { Switch(checked = ipv4, onCheckedChange = null) },
        )

        ListItem(
            headlineContent = { Text(text = stringResource(Res.string.ipv6)) },
            modifier = Modifier.heightIn(min = 68.dp).clickable { onToggleIpv6(!ipv6) },
            trailingContent = { Switch(checked = ipv6, onCheckedChange = null) },
        )
    }
}

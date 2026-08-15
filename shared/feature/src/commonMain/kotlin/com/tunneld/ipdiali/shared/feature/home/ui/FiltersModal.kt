package com.tunneld.ipdiali.shared.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NetworkCell
import androidx.compose.material.icons.outlined.NetworkWifi
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tunneld.ipdiali.shared.core.domain.NetworkType
import com.tunneld.ipdiali.shared.core.feature.ui.FindMyIpTheme
import com.tunneld.ipdiali.shared.feature.home.persentation.Filter
import com.tunneld.ipdiali.shared.feature.home.persentation.InternetProtocolVersion
import tunneld.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FiltersModal(
    filter: Filter,
    onDismiss: () -> Unit,
    onUpdateFilter: (Filter) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
        content = { FiltersModalContent(filter, onUpdateFilter) },
    )
}

@Composable
private fun FiltersModalContent(
    filter: Filter,
    onUpdateFilter: (Filter) -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchInput by remember(filter.query) { mutableStateOf(filter.query ?: "") }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(Res.string.headline_filters),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        // Internet Protocol filter chips
        Text(
            text = stringResource(Res.string.headline_internet_protocol),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                FilterChip(
                    selected = filter.protocols.contains(InternetProtocolVersion.IPV4),
                    onClick = {
                        onUpdateFilter(filter.toggleInternetProtocol(InternetProtocolVersion.IPV4))
                    },
                    label = { Text(stringResource(Res.string.ipv4)) },
                )
            }
            item {
                FilterChip(
                    selected = filter.protocols.contains(InternetProtocolVersion.IPV6),
                    onClick = {
                        onUpdateFilter(filter.toggleInternetProtocol(InternetProtocolVersion.IPV6))
                    },
                    label = { Text(stringResource(Res.string.ipv6)) },
                )
            }
        }

        // Network type filter chips
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.headline_network_type),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                FilterChip(
                    selected = filter.networkTypes.contains(NetworkType.WiFi),
                    onClick = { onUpdateFilter(filter.toggleNetworkType(NetworkType.WiFi)) },
                    label = { Text(stringResource(Res.string.wifi)) },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.NetworkWifi,
                            contentDescription = null,
                            modifier = Modifier.padding(start = 4.dp),
                        )
                    },
                )
            }
            item {
                FilterChip(
                    selected = filter.networkTypes.contains(NetworkType.Cellular),
                    onClick = {
                        onUpdateFilter(filter.toggleNetworkType(NetworkType.Cellular))
                    },
                    label = { Text(stringResource(Res.string.cellular)) },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.NetworkCell,
                            contentDescription = null,
                            modifier = Modifier.padding(start = 4.dp),
                        )
                    },
                )
            }
            item {
                FilterChip(
                    selected = filter.networkTypes.contains(NetworkType.VPN),
                    onClick = { onUpdateFilter(filter.toggleNetworkType(NetworkType.VPN)) },
                    label = { Text(stringResource(Res.string.vpn)) },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.VpnKey,
                            contentDescription = null,
                            modifier = Modifier.padding(start = 4.dp),
                        )
                    },
                )
            }
        }

        // Unified search (country or IP)
        Spacer(Modifier.height(8.dp))
        Text(
            text = "String",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        OutlinedTextField(
            value = searchInput,
            onValueChange = { value ->
                searchInput = value
                onUpdateFilter(filter.setQuery(value.ifBlank { null }))
            },
            label = { Text("IP or country") },
            placeholder = { Text("e.g. 192.168 or Morocco") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        )

        Spacer(Modifier.height(16.dp))
    }
}

@Preview
@Composable
private fun FiltersModalContentPreview() {
    FindMyIpTheme { Surface { FiltersModalContent(filter = Filter(setOf()), onUpdateFilter = {}) } }
}
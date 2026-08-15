package com.tunneld.ipdiali.feature.background.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.tunneld.ipdiali.shared.core.feature.ui.ArrowBackIconButton
import com.tunneld.ipdiali.shared.core.feature.ui.FindMyIpTheme
import tunneld.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun RunInBackgroundScreen(
    onBack: () -> Unit,
    periodicRefreshRunning: Boolean,
    realTimeMonitorRunning: Boolean,
    onTogglePeriodicRefresh: (Boolean) -> Unit,
    onToggleRealTimeMonitor: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_run_in_background)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues,
        ) {
            item {
                ListItem(
                    headlineContent = {
                        Text(stringResource(Res.string.headline_periodic_refresh))
                    },
                    modifier =
                        Modifier.clickable { onTogglePeriodicRefresh(!periodicRefreshRunning) },
                    supportingContent = {
                        Text("Run periodically in the background")
                    },
                    leadingContent = {
                        Icon(Icons.Outlined.Schedule, null)
                    },
                    trailingContent = {
                        Switch(checked = periodicRefreshRunning, onCheckedChange = null)
                    },
                )
            }
            item {
                ListItem(
                    headlineContent = {
                        Text("Real-time detection*")
                    },
                    modifier =
                        Modifier.clickable { onToggleRealTimeMonitor(!realTimeMonitorRunning) },
                    supportingContent = {
                        Column {
                            Text("Detects IP changes instantly when networks switch. Consumes more battery")
                            Text(
                                "*A persistent notification is mandatory to keep the service alive",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp),
                            )
                        }
                    },
                    leadingContent = {
                        Icon(Icons.Outlined.Bolt, null)
                    },
                    trailingContent = {
                        Switch(checked = realTimeMonitorRunning, onCheckedChange = null)
                    },
                )
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Preview
@Composable
private fun RunInBackgroundScreenPreview() {
    FindMyIpTheme {
        RunInBackgroundScreen(
            onBack = {},
            periodicRefreshRunning = true,
            realTimeMonitorRunning = false,
            onTogglePeriodicRefresh = {},
            onToggleRealTimeMonitor = {},
        )
    }
}
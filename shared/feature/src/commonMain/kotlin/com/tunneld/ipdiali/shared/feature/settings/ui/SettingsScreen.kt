package com.tunneld.ipdiali.shared.feature.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.TravelExplore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.tunneld.ipdiali.shared.core.feature.ui.ArrowBackIconButton
import com.tunneld.ipdiali.shared.core.feature.ui.ThemeState
import com.tunneld.ipdiali.shared.core.domain.ThemeMode
import com.tunneld.ipdiali.shared.core.application.usecase.ClearHistoryUseCase
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import tunneld.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun SettingsScreen(
    onBack: () -> Unit,
    onRunInBackground: () -> Unit,
    onNotifications: () -> Unit,
    onExportCsv: (String) -> Unit = {},
    onImportCsv: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var showDataSourceDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val clearHistoryUseCase: ClearHistoryUseCase = koinInject()

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_settings)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(vertical = 8.dp),
        ) {
            // === Behavior ===
            item { SettingsSectionHeader("Behavior") }
            item {
                ListItem(
                    headlineContent = {
                        Text(stringResource(Res.string.headline_run_in_background))
                    },
                    modifier = Modifier.heightIn(min = 68.dp).clickable { onRunInBackground() },
                    leadingContent = { Icon(Icons.Outlined.Engineering, null) },
                    supportingContent = {
                        Text(stringResource(Res.string.description_run_in_background))
                    },
                )
            }
            item {
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.headline_notifications)) },
                    modifier = Modifier.heightIn(min = 68.dp).clickable { onNotifications() },
                    leadingContent = { Icon(Icons.Outlined.Notifications, null) },
                    supportingContent = {
                        Text(stringResource(Res.string.description_notifications))
                    },
                )
            }

            // === Data & Backup ===
            item { SettingsSectionHeader("Data & Backup") }
            item {
                ListItem(
                    headlineContent = { Text("Geo IP Data Source") },
                    modifier = Modifier.heightIn(min = 68.dp).clickable { showDataSourceDialog = true },
                    leadingContent = { Icon(Icons.Outlined.TravelExplore, null) },
                    supportingContent = { Text("Choose geolocation provider") },
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Export CSV") },
                    modifier = Modifier.heightIn(min = 68.dp).clickable { onExportCsv("") },
                    leadingContent = { Icon(Icons.Outlined.FileDownload, null) },
                    supportingContent = { Text("Save history as CSV file") },
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Import CSV") },
                    modifier = Modifier.heightIn(min = 68.dp).clickable { onImportCsv() },
                    leadingContent = { Icon(Icons.Outlined.FileUpload, null) },
                    supportingContent = { Text("Import previously exported history") },
                )
            }

            // === Appearance ===
            item { SettingsSectionHeader("Appearance") }
            item {
                ListItem(
                    headlineContent = { Text("Theme") },
                    modifier = Modifier.heightIn(min = 68.dp).clickable { showThemeDialog = true },
                    leadingContent = { Icon(Icons.Outlined.Palette, null) },
                    supportingContent = { Text("System / Dark / Light") },
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Translucent Top Bar") },
                    supportingContent = { Text("When off, top bar uses solid background") },
                    trailingContent = {
                        val isTranslucent = ThemeState.isTopBarTranslucent
                        Switch(
                            checked = isTranslucent,
                            onCheckedChange = { ThemeState.isTopBarTranslucent = it },
                        )
                    },
                    modifier = Modifier.heightIn(min = 68.dp),
                )
            }

            // === Red Zone ===
            item { SettingsSectionHeader("Red Zone") }
            item {
                ListItem(
                    headlineContent = { Text("Clear History") },
                    modifier = Modifier.heightIn(min = 68.dp).clickable { showClearDialog = true },
                    leadingContent = {
                        Icon(Icons.Outlined.DeleteForever, null, tint = MaterialTheme.colorScheme.error)
                    },
                    supportingContent = { Text("Delete all stored IP addresses") },
                )
            }
        }
    }

    if (showDataSourceDialog) {
        GeoIpDataSourceDialog(
            onDismiss = { showDataSourceDialog = false },
        )
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear all history?") },
            text = { Text("This permanently deletes all stored IP addresses. This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            clearHistoryUseCase.clear()
                            showClearDialog = false
                        }
                    },
                ) {
                    Text("Delete All", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }

    if (showThemeDialog) {
        val currentTheme = ThemeState.themeMode
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Theme") },
            text = {
                Column(modifier = Modifier.selectableGroup()) {
                    ThemeMode.entries.forEach { mode ->
                        ListItem(
                            headlineContent = {
                                Text(
                                    when (mode) {
                                        ThemeMode.System -> "System default"
                                        ThemeMode.Dark -> "Dark"
                                        ThemeMode.Light -> "Light"
                                    }
                                )
                            },
                            leadingContent = {
                                RadioButton(
                                    selected = currentTheme == mode,
                                    onClick = {
                                        ThemeState.themeMode = mode
                                        showThemeDialog = false
                                    },
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = currentTheme == mode,
                                    onClick = {
                                        ThemeState.themeMode = mode
                                        showThemeDialog = false
                                    },
                                    role = Role.RadioButton,
                                ),
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Close")
                }
            },
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    )
}

@Composable
private fun GeoIpDataSourceDialog(onDismiss: () -> Unit) {
    val viewModel: GeoIpSettingsViewModel = koinViewModel()
    val selected by viewModel.selectedProvider.collectAsState()
    val apiKey by viewModel.apiKey.collectAsState()
    val needsKey = viewModel.selectedProviderObj?.requiresApiKey == true
    val signupUrl = viewModel.selectedProviderObj?.signupUrl

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Geo IP Data Source") },
        text = {
            Column(modifier = Modifier.selectableGroup()) {
                Text(
                    "Select Provider",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                viewModel.providerList.forEach { provider ->
                    ListItem(
                        headlineContent = { Text(provider.displayName) },
                        supportingContent = {
                            Text(if (provider.requiresApiKey) "API key required" else "No API key needed")
                        },
                        leadingContent = {
                            RadioButton(
                                selected = selected == provider.id,
                                onClick = { viewModel.selectProvider(provider.id) },
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selected == provider.id,
                                onClick = { viewModel.selectProvider(provider.id) },
                                role = Role.RadioButton,
                            ),
                    )
                }
                if (needsKey) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = { viewModel.updateApiKey(it) },
                        label = { Text("API Key") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )

                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                viewModel.saveApiKey()
                onDismiss()
            }) {
                Text("Done")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

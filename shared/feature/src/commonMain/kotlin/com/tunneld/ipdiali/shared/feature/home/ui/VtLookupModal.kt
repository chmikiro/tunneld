package com.tunneld.ipdiali.shared.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.tunneld.ipdiali.shared.core.infrastructure.ipapi.ExternalLinkPreferences
import com.tunneld.ipdiali.shared.core.infrastructure.ipapi.VtApiKeyPreferences
import com.tunneld.ipdiali.shared.core.infrastructure.virustotal.VtIpLookup
import com.tunneld.ipdiali.shared.core.infrastructure.virustotal.VtIpReport
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private fun isIpAddress(input: String): Boolean {
    val trimmed = input.trim()
    if (trimmed.contains(':')) return true // IPv6
    val octets = trimmed.split('.')
    return octets.size == 4 && octets.all { it.isNotEmpty() && it.toIntOrNull() != null }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VtLookupModal(
    onDismiss: () -> Unit,
    initialIp: String = "",
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState()
    val vtLookup: VtIpLookup = koinInject()
    val vtKeyPrefs: VtApiKeyPreferences = koinInject()
    val extPrefs: ExternalLinkPreferences = koinInject()
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()

    var input by remember { mutableStateOf(initialIp) }
    var apiKey by remember { mutableStateOf("") }
    var report by remember { mutableStateOf<VtIpReport?>(null) }
    var isScanning by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var skipConfirm by remember { mutableStateOf(false) }
    var pendingUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        apiKey = vtKeyPrefs.getVtApiKey() ?: ""
        skipConfirm = extPrefs.getSkipVtConfirmation()
    }

    fun openInVt() {
        val target = report?.target ?: input.trim()
        if (target.isBlank()) return
        val url =
            if (isIpAddress(target)) "https://www.virustotal.com/gui/ip-address/$target"
            else "https://www.virustotal.com/gui/domain/$target"
        if (skipConfirm) uriHandler.openUri(url)
        else pendingUrl = url
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "VirusTotal Lookup",
                style = MaterialTheme.typography.titleLarge,
            )

            if (apiKey.isBlank()) {
                Text(
                    "No API key set. Add your key in Settings → Data & Backup → VirusTotal API Key.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("IP address or domain") },
                placeholder = { Text("e.g. 8.8.8.8 or example.com") },
                singleLine = true,
                enabled = apiKey.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = {
                    scope.launch {
                        val target = input.trim()
                        isScanning = true
                        errorMsg = null
                        report =
                            if (isIpAddress(target)) vtLookup.lookupIp(target, apiKey)
                            else vtLookup.lookupDomain(target, apiKey)
                        isScanning = false
                        if (report == null) errorMsg = "Lookup failed or no report found."
                    }
                },
                enabled = apiKey.isNotBlank() && input.isNotBlank() && !isScanning,
            ) {
                Icon(Icons.Outlined.BugReport, null)
                Spacer(Modifier.width(6.dp))
                Text(if (isScanning) "Scanning..." else "Scan")
            }

            errorMsg?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            report?.let { r ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = r.target,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    val verdictColor =
                        if (r.isClean) Color(0xFF2E7D32)
                        else if (r.malicious > 0) MaterialTheme.colorScheme.error
                        else Color(0xFFF9A825)
                    Text(
                        text = if (r.isClean) "Clean" else "Flagged",
                        style = MaterialTheme.typography.titleMedium,
                        color = verdictColor,
                    )
                    VtStatRow("Harmless", r.harmless)
                    VtStatRow("Malicious", r.malicious)
                    VtStatRow("Suspicious", r.suspicious)
                    VtStatRow("Undetected", r.undetected)

                    OutlinedButton(
                        onClick = { openInVt() },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Outlined.OpenInNew, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Open in VirusTotal")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    pendingUrl?.let { url ->
        ExternalLinkConfirmationDialog(
            title = "Open in external browser?",
            message = url,
            skipChecked = skipConfirm,
            onSkipCheckedChange = { skipConfirm = it },
            onConfirm = {
                scope.launch {
                    extPrefs.setSkipVtConfirmation(skipConfirm)
                }
                uriHandler.openUri(url)
                pendingUrl = null
            },
            onDismiss = { pendingUrl = null },
        )
    }
}

@Composable
private fun VtStatRow(label: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(count.toString(), style = MaterialTheme.typography.bodyMedium)
    }
}

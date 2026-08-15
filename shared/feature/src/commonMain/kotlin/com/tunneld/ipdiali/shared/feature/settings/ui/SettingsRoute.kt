package com.tunneld.ipdiali.shared.feature.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.tunneld.ipdiali.shared.core.application.usecase.ExportAddressHistoryUseCase
import com.tunneld.ipdiali.shared.core.domain.NetworkType
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun SettingsRoute(
    onBack: () -> Unit,
    onRunInBackground: () -> Unit,
    onNotifications: () -> Unit,
    onExportCsv: (String) -> Unit,
    onImportCsv: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val exportUseCase: ExportAddressHistoryUseCase = koinInject()

    SettingsScreen(
        onBack = onBack,
        onRunInBackground = onRunInBackground,
        onNotifications = onNotifications,
        onExportCsv = {
            scope.launch {
                val csv = try {
                    exportUseCase.export(
                        query = null, ipv4 = true, ipv6 = true, networkTypes = setOf(NetworkType.WiFi, NetworkType.Cellular, NetworkType.VPN, NetworkType.Unknown),
                    ).let { results ->
                        val sb = StringBuilder()
                        sb.appendLine("address,version,network_type,country,country_code,city,isp,org,asn,timezone,latitude,longitude,timestamp")
                        results.forEach { item ->
                            val info = when (item) {
                                is com.tunneld.ipdiali.shared.core.domain.AddressHistory.Ipv4 -> item.ipInfo
                                is com.tunneld.ipdiali.shared.core.domain.AddressHistory.Ipv6 -> item.ipInfo
                            }
                            val version = when (item) {
                                is com.tunneld.ipdiali.shared.core.domain.AddressHistory.Ipv4 -> "IPv4"
                                is com.tunneld.ipdiali.shared.core.domain.AddressHistory.Ipv6 -> "IPv6"
                            }
                            val nt = item.networkType.toString().substringAfterLast(".")
                            sb.appendLine(
                                listOf(
                                    item.stringRepresentation(), version, nt,
                                    info?.country ?: "", info?.countryCode ?: "",
                                    info?.city ?: "", info?.isp ?: "", info?.org ?: "",
                                    info?.asn ?: "", info?.timezone ?: "",
                                    info?.latitude?.toString() ?: "", info?.longitude?.toString() ?: "",
                                    item.dateTime.toString(),
                                ).joinToString(",") { "\"${it.replace("\"", "\"\"")}\"" }
                            )
                        }
                        sb.toString()
                    }
                } catch (_: Exception) { "" }
                onExportCsv(csv)
            }
        },
        onImportCsv = onImportCsv,
        modifier = modifier,
    )
}
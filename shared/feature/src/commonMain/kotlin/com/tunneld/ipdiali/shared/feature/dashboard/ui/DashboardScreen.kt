package com.tunneld.ipdiali.shared.feature.dashboard.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.tunneld.ipdiali.shared.core.feature.ui.ArrowBackIconButton
import com.tunneld.ipdiali.shared.core.application.usecase.ExportAddressHistoryUseCase
import com.tunneld.ipdiali.shared.core.domain.NetworkType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DashboardScreen(
    onBack: () -> Unit,
    exportUseCase: ExportAddressHistoryUseCase,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var csvContent by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        csvContent = try {
            exportUseCase.export(
                query = null,
                ipv4 = true,
                ipv6 = true,
                country = null,
                networkTypes = NetworkType.entries.toSet(),
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
                            item.stringRepresentation(),
                            version,
                            nt,
                            info?.country ?: "",
                            info?.countryCode ?: "",
                            info?.city ?: "",
                            info?.isp ?: "",
                            info?.org ?: "",
                            info?.asn ?: "",
                            info?.timezone ?: "",
                            info?.latitude?.toString() ?: "",
                            info?.longitude?.toString() ?: "",
                            item.dateTime.toString(),
                        ).joinToString(",") { "\"${it.replace("\"", "\"\"")}\"" }
                    )
                }
                sb.toString()
            }
        } catch (_: Exception) {
            ""
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text("Dashboard") },
                navigationIcon = { ArrowBackIconButton(onClick = onBack) },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeFlexibleTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            csvContent?.let { csv ->
                DashboardContent(csvContent = csv, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

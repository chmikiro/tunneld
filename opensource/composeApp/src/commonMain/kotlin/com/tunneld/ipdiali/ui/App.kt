package com.tunneld.ipdiali.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.tunneld.ipdiali.navigation.AppNavHost
import com.tunneld.ipdiali.shared.core.feature.ui.FindMyIpTheme

@Composable
internal fun App(onExportCsv: (csvContent: String) -> Unit = {}) {
    FindMyIpTheme { Surface { AppNavHost(onExportCsv = onExportCsv) } }
}

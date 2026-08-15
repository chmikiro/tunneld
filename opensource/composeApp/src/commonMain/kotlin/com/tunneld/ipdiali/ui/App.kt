package com.tunneld.ipdiali.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.tunneld.ipdiali.navigation.AppNavHost
import com.tunneld.ipdiali.shared.core.feature.ui.FindMyIpTheme
import com.tunneld.ipdiali.shared.core.feature.ui.ThemeState

@Composable
internal fun App(
    onExportCsv: (csvContent: String) -> Unit = {},
    onImportCsv: () -> Unit = {},
    dashboardComposable: @Composable (onBack: () -> Unit) -> Unit = { {} },
) {
    FindMyIpTheme(themeMode = ThemeState.themeMode) {
        Surface {
            AppNavHost(
                onExportCsv = onExportCsv,
                onImportCsv = onImportCsv,
                dashboardComposable = dashboardComposable,
            )
        }
    }
}
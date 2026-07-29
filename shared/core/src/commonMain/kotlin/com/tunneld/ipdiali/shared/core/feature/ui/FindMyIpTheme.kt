package com.tunneld.ipdiali.shared.core.feature.ui

import androidx.compose.runtime.Composable
import com.tunneld.ipdiali.shared.core.domain.ThemeMode

@Composable expect fun FindMyIpTheme(
    themeMode: ThemeMode = ThemeMode.System,
    content: @Composable () -> Unit,
)

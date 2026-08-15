package com.tunneld.ipdiali.shared.core.feature.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tunneld.ipdiali.shared.core.domain.ThemeMode

object ThemeState {
    var isTopBarTranslucent by mutableStateOf(false)
    var themeMode: ThemeMode by mutableStateOf(ThemeMode.System)
}
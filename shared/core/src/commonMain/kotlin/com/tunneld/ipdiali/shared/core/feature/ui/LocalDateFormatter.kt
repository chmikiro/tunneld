package com.tunneld.ipdiali.shared.core.feature.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.tunneld.ipdiali.shared.core.feature.presentation.DateFormatter

val LocalDateFormatter = staticCompositionLocalOf { DateFormatter.noop }

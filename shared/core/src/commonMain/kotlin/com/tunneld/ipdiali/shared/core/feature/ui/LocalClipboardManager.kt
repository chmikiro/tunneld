package com.tunneld.ipdiali.shared.core.feature.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.tunneld.ipdiali.shared.core.feature.presentation.ClipboardManager

val LocalClipboardManager = staticCompositionLocalOf { ClipboardManager.noop }

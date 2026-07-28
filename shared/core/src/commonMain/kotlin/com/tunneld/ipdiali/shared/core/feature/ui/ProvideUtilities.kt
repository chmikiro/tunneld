package com.tunneld.ipdiali.shared.core.feature.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.tunneld.ipdiali.shared.core.feature.presentation.ClipboardManager
import com.tunneld.ipdiali.shared.core.feature.presentation.DateFormatter

@Composable
fun ProvideUtilities(
    dateFormatter: DateFormatter,
    clipboardManager: ClipboardManager,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalDateFormatter provides dateFormatter,
        LocalClipboardManager provides clipboardManager,
    ) {
        content()
    }
}

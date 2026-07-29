package com.tunneld.ipdiali.shared.core.feature.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.tunneld.ipdiali.shared.core.domain.ThemeMode

@Composable
actual fun FindMyIpTheme(
    themeMode: ThemeMode,
    content: @Composable (() -> Unit),
) {
    val darkTheme = when (themeMode) {
        ThemeMode.Dark -> true
        ThemeMode.Light -> false
        ThemeMode.System -> isSystemInDarkTheme()
    }

    FindMyIpThemeImpl(darkTheme = darkTheme, dynamicColor = true, content = content)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FindMyIpThemeImpl(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    content: @Composable (() -> Unit),
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            darkTheme -> darkColorScheme(
                primary = Color(0xFFD0BCFF),
                secondary = Color(0xFFCCC2DC),
                tertiary = Color(0xFFEFB8C8),
            )
            else -> lightColorScheme(
                primary = Color(0xFF6650a4),
                secondary = Color(0xFF625b71),
                tertiary = Color(0xFF7D5260),
            )
        }

    MaterialExpressiveTheme(colorScheme = colorScheme, content = content)
}

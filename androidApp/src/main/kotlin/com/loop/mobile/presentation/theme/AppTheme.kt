package com.loop.mobile.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun AppTheme(
    themeManager: ThemeManager,
    content: @Composable () -> Unit
) {
    val overrideDarkMode by themeManager.overrideDarkMode.collectAsState()
    val isDark = overrideDarkMode ?: themeManager.isDarkMode()
    val colorScheme = if (isDark) {
        AppTheme.darkColors.toDarkColorScheme()
    } else {
        AppTheme.lightColors.toLightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

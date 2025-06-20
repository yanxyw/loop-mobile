package com.loop.mobile.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(
    themeProvider: ThemeProvider,
    content: @Composable () -> Unit
) {
    val appColorScheme = themeProvider.getCurrentColorScheme()
    val isDark = themeProvider.isDarkMode()

    val colorScheme = if (isDark) {
        appColorScheme.toDarkColorScheme()
    } else {
        appColorScheme.toLightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
//        typography = Typography(), // optionally create shared Typography
        content = content
    )
}

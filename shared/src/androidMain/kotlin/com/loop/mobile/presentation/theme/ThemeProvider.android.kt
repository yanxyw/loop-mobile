package com.loop.mobile.presentation.theme

import android.content.Context
import android.content.res.Configuration

actual class ThemeProvider actual constructor(context: Any) {
    private val androidContext = context as Context

    actual fun getCurrentColorScheme(): AppColorScheme {
        return if (isDarkMode()) {
            AppTheme.darkColors
        } else {
            AppTheme.lightColors
        }
    }

    actual fun isDarkMode(): Boolean {
        val nightModeFlags =
            androidContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }
}

// Extension functions for Android Color conversion
fun Long.toAndroidColor(): Int = this.toInt()
package com.loop.mobile.presentation.theme

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppColorScheme(
    val background: Long,
    val primary: Long,
    val onPrimary: Long,
    val surface: Long,
    val onSurface: Long
)

object AppTheme {
    val lightColors = AppColorScheme(
        background = AppColors.backgroundLight,
        primary = AppColors.primaryLight,
        onPrimary = AppColors.onPrimaryLight,
        surface = AppColors.surfaceLight,
        onSurface = AppColors.onSurfaceLight
    )

    val darkColors = AppColorScheme(
        background = AppColors.backgroundDark,
        primary = AppColors.primaryDark,
        onPrimary = AppColors.onPrimaryDark,
        surface = AppColors.surfaceDark,
        onSurface = AppColors.onSurfaceDark
    )
}

expect class ThemeProvider constructor(context: Any) {
    fun getCurrentColorScheme(): AppColorScheme
    fun isDarkMode(): Boolean
}

class ThemeManager(context: Any) {
    private val themeProvider = ThemeProvider(context)

    private val _overrideDarkMode = MutableStateFlow<Boolean?>(null)
    val overrideDarkMode: StateFlow<Boolean?> = _overrideDarkMode.asStateFlow()

    fun setOverrideDarkMode(enabled: Boolean?) {
        _overrideDarkMode.value = enabled
    }

    fun getCurrentColors(): AppColorScheme {
        val useDark = _overrideDarkMode.value ?: themeProvider.isDarkMode()
        return if (useDark) AppTheme.darkColors else AppTheme.lightColors
    }

    fun isDarkMode(): Boolean {
        return _overrideDarkMode.value ?: themeProvider.isDarkMode()
    }
}

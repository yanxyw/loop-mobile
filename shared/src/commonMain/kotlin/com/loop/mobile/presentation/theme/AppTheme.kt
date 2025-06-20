package com.loop.mobile.presentation.theme

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

// Theme provider interface
expect class ThemeProvider constructor(context: Any) {
    fun getCurrentColorScheme(): AppColorScheme
    fun isDarkMode(): Boolean
}

// Optional: Theme manager for switching themes
class ThemeManager(context: Any) {
    private val themeProvider = ThemeProvider(context)

    fun getCurrentColors(): AppColorScheme {
        return if (themeProvider.isDarkMode()) {
            AppTheme.darkColors
        } else {
            AppTheme.lightColors
        }
    }
}
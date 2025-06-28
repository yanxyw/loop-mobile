package com.loop.mobile.presentation.theme

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppColorScheme(
    val background: Long,
    val primary: Long,
    val onPrimary: Long,
    val surface: Long,
    val onSurface: Long,
    val outline: Long,
    val outlineVariant: Long,
    val error: Long,
    val onSurfaceVariant: Long
)

object AppTheme {
    val lightColors = AppColorScheme(
        background = AppColors.backgroundLight,
        primary = AppColors.primaryLight,
        onPrimary = AppColors.onPrimaryLight,
        surface = AppColors.surfaceLight,
        onSurface = AppColors.onSurfaceLight,
        outline = AppColors.outlineLight,
        outlineVariant = AppColors.outlineVariantLight,
        error = AppColors.errorLight,
        onSurfaceVariant = AppColors.onSurfaceVariantLight
    )

    val darkColors = AppColorScheme(
        background = AppColors.backgroundDark,
        primary = AppColors.primaryDark,
        onPrimary = AppColors.onPrimaryDark,
        surface = AppColors.surfaceDark,
        onSurface = AppColors.onSurfaceDark,
        outline = AppColors.outlineDark,
        outlineVariant = AppColors.outlineVariantDark,
        error = AppColors.errorDark,
        onSurfaceVariant = AppColors.onSurfaceVariantDark
    )
}

expect class ThemeProvider(context: Any) {
    fun isDarkMode(): Boolean
}

class ThemeManager(
    context: Any,
    private val storage: ThemeStorage
) {
    private val themeProvider = ThemeProvider(context)

    private val _overrideDarkMode = MutableStateFlow<Boolean?>(storage.getOverrideDarkMode())
    val overrideDarkMode: StateFlow<Boolean?> = _overrideDarkMode.asStateFlow()

    fun setOverrideDarkMode(enabled: Boolean?) {
        _overrideDarkMode.value = enabled
        storage.setOverrideDarkMode(enabled)
    }

    fun isDarkMode(): Boolean {
        return _overrideDarkMode.value ?: themeProvider.isDarkMode()
    }

    fun getCurrentColorScheme(): AppColorScheme {
        val useDark = isDarkMode()
        return if (useDark) AppTheme.darkColors else AppTheme.lightColors
    }
}
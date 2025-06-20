package com.loop.mobile.presentation.theme

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeObserver(private val themeProvider: ThemeProvider) {
    private val _currentTheme = MutableStateFlow(themeProvider.getCurrentColorScheme())
    val currentTheme: StateFlow<AppColorScheme> = _currentTheme.asStateFlow()

    fun updateTheme() {
        _currentTheme.value = themeProvider.getCurrentColorScheme()
    }

    fun isDarkMode(): Boolean = themeProvider.isDarkMode()
}
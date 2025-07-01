package com.loop.mobile

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.loop.mobile.presentation.MainScreen
import com.loop.mobile.presentation.theme.ThemeManager

@Composable
fun App(
    themeManager: ThemeManager,
) {
    MaterialTheme {
        MainScreen(
            themeManager = themeManager
        )
    }
}

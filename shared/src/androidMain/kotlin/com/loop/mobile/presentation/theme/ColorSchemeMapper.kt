package com.loop.mobile.presentation.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

fun AppColorScheme.toLightColorScheme(): ColorScheme = lightColorScheme(
    primary = Color(primary),
    onPrimary = Color(onPrimary),
    background = Color(background),
    surface = Color(surface),
    onSurface = Color(onSurface)
)

fun AppColorScheme.toDarkColorScheme(): ColorScheme = darkColorScheme(
    primary = Color(primary),
    onPrimary = Color(onPrimary),
    background = Color(background),
    surface = Color(surface),
    onSurface = Color(onSurface)
)
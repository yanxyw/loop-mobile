package com.loop.mobile.presentation.theme

import platform.UIKit.UIColor

class IOSColorScheme(
    val background: UIColor,
    val primary: UIColor,
    val onPrimary: UIColor,
    val surface: UIColor,
    val onSurface: UIColor,
    val outline: UIColor,
    val outlineVariant: UIColor,
    val error: UIColor,
    val onSurfaceVariant: UIColor
)

fun AppColorScheme.toIOSColorScheme(): IOSColorScheme = IOSColorScheme(
    background = background.toUIColor(),
    primary = primary.toUIColor(),
    onPrimary = onPrimary.toUIColor(),
    surface = surface.toUIColor(),
    onSurface = onSurface.toUIColor(),
    outline = outline.toUIColor(),
    outlineVariant = outlineVariant.toUIColor(),
    error = error.toUIColor(),
    onSurfaceVariant = onSurfaceVariant.toUIColor()
)

fun Long.toUIColor(): UIColor {
    val red = ((this shr 16) and 0xFF) / 255.0
    val green = ((this shr 8) and 0xFF) / 255.0
    val blue = (this and 0xFF) / 255.0
    val alpha = ((this shr 24) and 0xFF) / 255.0
    return UIColor(red = red, green = green, blue = blue, alpha = alpha)
}
package com.loop.mobile.presentation.theme

import platform.UIKit.UIScreen
import platform.UIKit.UIUserInterfaceStyle

actual class ThemeProvider actual constructor(context: Any) {

    actual fun getCurrentColorScheme(): AppColorScheme {
        return if (isDarkMode()) {
            AppTheme.darkColors
        } else {
            AppTheme.lightColors
        }
    }

    actual fun isDarkMode(): Boolean {
        val traitCollection = UIScreen.mainScreen.traitCollection
        return traitCollection.userInterfaceStyle == UIUserInterfaceStyle.UIUserInterfaceStyleDark
    }
}

// Extension functions for iOS UIColor conversion
fun Long.toUIColor(): platform.UIKit.UIColor {
    val red = ((this shr 16) and 0xFF) / 255.0
    val green = ((this shr 8) and 0xFF) / 255.0
    val blue = (this and 0xFF) / 255.0
    val alpha = ((this shr 24) and 0xFF) / 255.0

    return platform.UIKit.UIColor.colorWithRed(
        red = red,
        green = green,
        blue = blue,
        alpha = alpha
    )
}
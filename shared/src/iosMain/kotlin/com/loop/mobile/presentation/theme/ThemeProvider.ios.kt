package com.loop.mobile.presentation.theme

import platform.UIKit.UIScreen
import platform.UIKit.UIUserInterfaceStyle

actual class ThemeProvider actual constructor(context: Any) {

    actual fun isDarkMode(): Boolean {
        val traitCollection = UIScreen.mainScreen.traitCollection
        return traitCollection.userInterfaceStyle == UIUserInterfaceStyle.UIUserInterfaceStyleDark
    }
}
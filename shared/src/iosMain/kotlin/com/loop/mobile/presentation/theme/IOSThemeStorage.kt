package com.loop.mobile.presentation.theme

import platform.Foundation.NSUserDefaults

actual fun provideThemeStorage(context: Any): ThemeStorage = IosThemeStorage()

class IosThemeStorage : ThemeStorage {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun getOverrideDarkMode(): Boolean? {
        return if (defaults.objectForKey("override") != null)
            defaults.boolForKey("override")
        else
            null
    }

    override fun setOverrideDarkMode(value: Boolean?) {
        if (value != null) {
            defaults.setBool(value, forKey = "override")
        } else {
            defaults.removeObjectForKey("override")
        }
    }
}
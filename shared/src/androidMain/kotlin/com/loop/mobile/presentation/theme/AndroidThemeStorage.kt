package com.loop.mobile.presentation.theme

import android.content.Context

actual fun provideThemeStorage(context: Any): ThemeStorage = AndroidThemeStorage(context as Context)

class AndroidThemeStorage(context: Context) : ThemeStorage {
    private val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    override fun getOverrideDarkMode(): Boolean? =
        if (prefs.contains("override")) prefs.getBoolean("override", false) else null

    override fun setOverrideDarkMode(value: Boolean?) {
        with(prefs.edit()) {
            if (value == null) remove("override")
            else putBoolean("override", value)
            apply()
        }
    }
}
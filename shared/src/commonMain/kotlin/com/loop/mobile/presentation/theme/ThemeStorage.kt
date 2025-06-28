package com.loop.mobile.presentation.theme

expect fun provideThemeStorage(context: Any): ThemeStorage

interface ThemeStorage {
    fun getOverrideDarkMode(): Boolean?
    fun setOverrideDarkMode(value: Boolean?)
}


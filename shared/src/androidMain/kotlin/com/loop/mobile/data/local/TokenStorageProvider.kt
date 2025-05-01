package com.loop.mobile.data.local

import android.content.Context

private lateinit var appContext: Context

fun initTokenStorage(context: Context) {
    appContext = context.applicationContext
}

actual fun provideTokenStorage(): TokenStorage {
    check(::appContext.isInitialized) { "appContext is not initialized" }
    return TokenStorageImpl(appContext)
}
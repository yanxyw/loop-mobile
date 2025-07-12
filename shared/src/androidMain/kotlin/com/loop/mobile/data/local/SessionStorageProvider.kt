package com.loop.mobile.data.local

import android.content.Context

private lateinit var appContext: Context

fun initSessionStorage(context: Context) {
    appContext = context.applicationContext
}

actual fun provideSessionStorage(): SessionStorage {
    check(::appContext.isInitialized) { "appContext is not initialized" }
    return SessionStorageImpl(appContext)
}
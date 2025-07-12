package com.loop.mobile.data.local

actual fun provideSessionStorage(): SessionStorage {
    return SessionStorageImpl()
}
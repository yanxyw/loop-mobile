package com.loop.mobile.data.local

actual fun provideTokenStorage(): TokenStorage {
    return TokenStorageImpl()
}
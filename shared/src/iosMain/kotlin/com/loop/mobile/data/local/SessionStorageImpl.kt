package com.loop.mobile.data.local

import platform.Foundation.NSUserDefaults

class SessionStorageImpl : SessionStorage {

    private val defaults = NSUserDefaults.standardUserDefaults

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        defaults.setObject(accessToken, forKey = "accessToken")
        defaults.setObject(refreshToken, forKey = "refreshToken")
    }

    override suspend fun getAccessToken(): String? =
        defaults.stringForKey("accessToken")

    override suspend fun getRefreshToken(): String? =
        defaults.stringForKey("refreshToken")


    override suspend fun saveAuthProvider(provider: String) {
        defaults.setObject(provider, forKey = "authProvider")
    }

    override suspend fun getAuthProvider(): String? =
        defaults.stringForKey("authProvider")

    override suspend fun clearSession() {
        defaults.removeObjectForKey("accessToken")
        defaults.removeObjectForKey("refreshToken")
        defaults.removeObjectForKey("authProvider")
    }
}

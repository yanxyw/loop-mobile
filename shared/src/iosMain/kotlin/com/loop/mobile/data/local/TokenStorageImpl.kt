package com.loop.mobile.data.local

import platform.Foundation.NSUserDefaults

class TokenStorageImpl : TokenStorage {

    private val defaults = NSUserDefaults.standardUserDefaults

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        defaults.setObject(accessToken, forKey = "accessToken")
        defaults.setObject(refreshToken, forKey = "refreshToken")
    }

    override suspend fun getAccessToken(): String? =
        defaults.stringForKey("accessToken")

    override suspend fun getRefreshToken(): String? =
        defaults.stringForKey("refreshToken")

    override suspend fun clearTokens() {
        defaults.removeObjectForKey("accessToken")
        defaults.removeObjectForKey("refreshToken")
    }
}

package com.loop.mobile.data.local

import android.content.Context

class TokenStorageImpl(private val context: Context) : TokenStorage {

    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit()
            .putString("accessToken", accessToken)
            .putString("refreshToken", refreshToken)
            .apply()
    }

    override suspend fun getAccessToken(): String? = prefs.getString("accessToken", null)

    override suspend fun getRefreshToken(): String? = prefs.getString("refreshToken", null)

    override suspend fun clearTokens() {
        prefs.edit().clear().apply()
    }
}

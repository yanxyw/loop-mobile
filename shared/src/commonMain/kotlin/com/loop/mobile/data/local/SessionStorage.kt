package com.loop.mobile.data.local

interface SessionStorage {
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun saveAuthProvider(provider: String)
    suspend fun getAuthProvider(): String?
    suspend fun clearSession()
}
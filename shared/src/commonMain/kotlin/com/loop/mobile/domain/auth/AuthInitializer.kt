package com.loop.mobile.domain.auth

import com.loop.mobile.data.local.TokenStorage
import com.loop.mobile.domain.utils.JwtUtils

suspend fun restoreAuthState(
    tokenStorage: TokenStorage,
    authStateManager: AuthStateManager
) {
    val accessToken = tokenStorage.getAccessToken()
    if (!accessToken.isNullOrBlank()) {
        val decodedUser = JwtUtils.decodeUser(accessToken)
        authStateManager.setUser(decodedUser)
    }
}
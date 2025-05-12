package com.loop.mobile.domain.auth

import com.loop.mobile.data.local.TokenStorage
import com.loop.mobile.data.mappers.toDecodedUser
import com.loop.mobile.domain.repositories.UserRepository
import com.loop.mobile.domain.utils.JwtUtils

suspend fun restoreAuthState(
    tokenStorage: TokenStorage,
    userRepository: UserRepository,
    authStateManager: AuthStateManager
) {
    val accessToken = tokenStorage.getAccessToken()
    val refreshToken = tokenStorage.getRefreshToken()

    if (!accessToken.isNullOrBlank()) {
        if (JwtUtils.isTokenExpired(accessToken)) {
            // Token is expired but there might be a refresh token
            if (!refreshToken.isNullOrBlank()) {
                // Try to refresh by calling the profile API
                val result = userRepository.getMe()

                result.fold(
                    onSuccess = { user ->
                        authStateManager.setUser(user.toDecodedUser())
                    },
                    onFailure = {
                        authStateManager.setUser(null)
                    }
                )
            } else {
                // No refresh token, clear user state
                authStateManager.setUser(null)
            }
        } else {
            // Token is still valid, use the decoded user
            val decodedUser = JwtUtils.decodeUser(accessToken)
            authStateManager.setUser(decodedUser)
        }
    } else {
        // No access token, clear user state
        authStateManager.setUser(null)
    }
}
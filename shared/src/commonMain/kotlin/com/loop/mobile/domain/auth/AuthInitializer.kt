package com.loop.mobile.domain.auth

import com.loop.mobile.data.local.SessionStorage
import com.loop.mobile.data.mappers.toDecodedUser
import com.loop.mobile.domain.repositories.UserRepository
import com.loop.mobile.domain.utils.JwtUtils

suspend fun restoreAuthState(
    sessionStorage: SessionStorage,
    userRepository: UserRepository,
    authStateManager: AuthStateManager
) {
    val accessToken = sessionStorage.getAccessToken()
    val refreshToken = sessionStorage.getRefreshToken()
    val provider = sessionStorage.getAuthProvider()

    if (!accessToken.isNullOrBlank()) {
        if (JwtUtils.isTokenExpired(accessToken)) {
            // Token is expired but there might be a refresh token
            if (!refreshToken.isNullOrBlank()) {
                // Try to refresh by calling the profile API
                val result = userRepository.getMe()

                result.fold(
                    onSuccess = { user ->
                        authStateManager.setUser(user?.toDecodedUser(), provider)
                    },
                    onFailure = {
                        authStateManager.clearUser()
                    }
                )
            } else {
                // No refresh token, clear user state
                authStateManager.clearUser()
            }
        } else {
            // Token is still valid, use the decoded user
            val decodedUser = JwtUtils.decodeUser(accessToken)
            authStateManager.setUser(decodedUser, provider)
        }
    } else {
        // No access token, clear user state
        authStateManager.clearUser()
    }
}
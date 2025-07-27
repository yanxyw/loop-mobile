package com.loop.mobile.domain.usecases

import com.loop.mobile.data.local.SessionStorage
import com.loop.mobile.data.remote.network.ApiResult
import com.loop.mobile.data.remote.network.ApiResult.Error
import com.loop.mobile.data.remote.network.ApiResult.Success
import com.loop.mobile.domain.auth.AuthStateManager
import com.loop.mobile.domain.repositories.AuthRepository
import com.loop.mobile.domain.repositories.OAuthRepository
import com.loop.mobile.domain.utils.JwtUtils

class LoginUseCase(
    private val authRepository: AuthRepository,
    private val oauthRepository: OAuthRepository,
    private val sessionStorage: SessionStorage,
    private val authStateManager: AuthStateManager
) {
    suspend operator fun invoke(email: String, password: String): ApiResult<Unit> {
        return when (val result = authRepository.login(email, password)) {
            is Success -> {
                val auth = result.data
                if (auth != null) {
                    sessionStorage.saveTokens(auth.accessToken, auth.refreshToken)
                    authStateManager.setUser(JwtUtils.decodeUser(auth.accessToken), "password")
                }
                sessionStorage.saveAuthProvider("password")
                Success(result.code, result.message, Unit)
            }

            is Error -> Error(result.code, result.message, result.exception)
        }
    }

    suspend operator fun invoke(
        provider: String,
        code: String,
        redirectUri: String
    ): ApiResult<Unit> {
        return when (val result = oauthRepository.oauthLogin(provider, code, redirectUri)) {
            is Success -> {
                val auth = result.data
                if (auth != null) {
                    sessionStorage.saveTokens(auth.accessToken, auth.refreshToken)
                    authStateManager.setUser(JwtUtils.decodeUser(auth.accessToken), provider)
                }
                sessionStorage.saveAuthProvider(provider)
                Success( result.code, result.message, Unit)
            }

            is Error -> Error(result.code, result.message, result.exception)
        }
    }
}
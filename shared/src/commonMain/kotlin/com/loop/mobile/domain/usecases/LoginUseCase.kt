package com.loop.mobile.domain.usecases

import com.loop.mobile.data.local.TokenStorage
import com.loop.mobile.data.remote.network.ApiResult
import com.loop.mobile.data.remote.network.ApiResult.Error
import com.loop.mobile.data.remote.network.ApiResult.Success
import com.loop.mobile.domain.auth.AuthStateManager
import com.loop.mobile.domain.repositories.AuthRepository
import com.loop.mobile.domain.utils.JwtUtils

class LoginUseCase(
    private val authRepository: AuthRepository,
    private val tokenStorage: TokenStorage,
    private val authStateManager: AuthStateManager
) {
    suspend operator fun invoke(email: String, password: String): ApiResult<Unit> {
        return when (val result = authRepository.login(email, password)) {
            is Success -> {
                val auth = result.data
                tokenStorage.saveTokens(auth.accessToken, auth.refreshToken)
                authStateManager.setUser(JwtUtils.decodeUser(auth.accessToken))
                Success(Unit, result.code, result.message)
            }

            is Error -> Error(result.code, result.message, result.exception)
        }
    }
}
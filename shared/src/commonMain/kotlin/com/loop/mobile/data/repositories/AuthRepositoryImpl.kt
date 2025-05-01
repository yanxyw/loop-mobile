package com.loop.mobile.data.repositories

import com.loop.mobile.data.mappers.AuthMapper
import com.loop.mobile.data.remote.network.ApiResult
import com.loop.mobile.data.remote.network.ApiResult.Error
import com.loop.mobile.data.remote.network.ApiResult.Success
import com.loop.mobile.data.remote.services.AuthService
import com.loop.mobile.domain.entities.AuthResult
import com.loop.mobile.domain.repositories.AuthRepository

class AuthRepositoryImpl(
    private val authService: AuthService,
    private val authMapper: AuthMapper
) : AuthRepository {

    override suspend fun login(email: String, password: String): ApiResult<AuthResult> {
        return when (val result = authService.login(email, password)) {
            is Success -> {
                // Store tokens in secure storage if needed
                storeTokens(result.data.accessToken, result.data.refreshToken)

                // Map to domain entity (AuthResult)
                val authResult = authMapper.mapLoginResponseToDomain(result.data)
                Success(authResult, result.code, result.message)
            }

            is Error -> {
                // Map error to appropriate error message
                Error(result.code, result.message, result.exception)
            }
        }
    }

    private fun storeTokens(accessToken: String, refreshToken: String) {
        // Implement token storage in a platform-specific way
        // This could use expect/actual for secure storage
    }
}
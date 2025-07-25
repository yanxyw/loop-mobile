package com.loop.mobile.data.repositories

import com.loop.mobile.data.local.SessionStorage
import com.loop.mobile.data.mappers.toDomain
import com.loop.mobile.data.remote.dto.LoginRequestDto
import com.loop.mobile.data.remote.dto.SignUpRequestDto
import com.loop.mobile.data.remote.network.ApiResult
import com.loop.mobile.data.remote.network.ApiResult.Error
import com.loop.mobile.data.remote.network.ApiResult.Success
import com.loop.mobile.data.remote.services.auth.AuthService
import com.loop.mobile.domain.auth.AuthStateManager
import com.loop.mobile.domain.entities.AuthResult
import com.loop.mobile.domain.repositories.AuthRepository

class AuthRepositoryImpl(
    private val authService: AuthService,
    private val sessionStorage: SessionStorage,
    private val authStateManager: AuthStateManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): ApiResult<AuthResult> {
        val dto = LoginRequestDto(email, password)
        return when (val result = authService.login(dto)) {
            is Success -> Success(result.data.toDomain(), result.code, result.message)
            is Error -> Error(result.code, result.message, result.exception)
        }
    }

    override suspend fun signUp(email: String, password: String, username: String): ApiResult<String> {
        val dto = SignUpRequestDto(email, password, username)
        return when (val result = authService.signUp(dto)) {
            is Success -> Success(result.data, result.code, result.message)
            is Error -> Error(result.code, result.message, result.exception)
        }
    }

    override suspend fun logout(): ApiResult<String> {
        return when (val result = authService.logout()) {
            is Success -> {
                sessionStorage.clearSession()
                authStateManager.clearUser()
                Success(result.data, result.code, result.message)
            }

            is Error -> Error(result.code, result.message, result.exception)
        }
    }
}

package com.loop.mobile.domain.repositories

import com.loop.mobile.data.remote.network.ApiResult
import com.loop.mobile.domain.entities.AuthResult

interface AuthRepository {
    suspend fun login(email: String, password: String): ApiResult<AuthResult>

    suspend fun signUp(email: String, password: String, username: String): ApiResult<String>

    suspend fun checkEmail(email: String): ApiResult<String>

    suspend fun logout(): ApiResult<String>
}
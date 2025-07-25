package com.loop.mobile.data.remote.services.auth

import com.loop.mobile.data.remote.dto.LoginRequestDto
import com.loop.mobile.data.remote.dto.LoginResponseDto
import com.loop.mobile.data.remote.dto.SignUpRequestDto
import com.loop.mobile.data.remote.network.ApiResult

interface AuthService {
    suspend fun login(request: LoginRequestDto): ApiResult<LoginResponseDto>

    suspend fun signUp(request: SignUpRequestDto): ApiResult<String>

    suspend fun logout(): ApiResult<String>
}
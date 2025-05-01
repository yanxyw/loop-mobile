package com.loop.mobile.data.remote.services

import com.loop.mobile.data.remote.dto.LoginResponseDto
import com.loop.mobile.data.remote.network.ApiResult

interface AuthService {
    suspend fun login(email: String, password: String): ApiResult<LoginResponseDto>
}
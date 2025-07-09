package com.loop.mobile.data.remote.services.oauth

import com.loop.mobile.data.remote.dto.LoginResponseDto
import com.loop.mobile.data.remote.network.ApiResult

interface OAuthService {
    suspend fun oauthLogin(provider: String, code: String, redirectUri: String): ApiResult<LoginResponseDto>
}
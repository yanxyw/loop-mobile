package com.loop.mobile.domain.repositories

import com.loop.mobile.data.remote.network.ApiResult
import com.loop.mobile.domain.entities.AuthResult

interface OAuthRepository {
    suspend fun oauthLogin(provider: String, code: String, redirectUri: String): ApiResult<AuthResult>
}
package com.loop.mobile.data.repositories

import com.loop.mobile.data.mappers.toDomain
import com.loop.mobile.data.remote.network.ApiResult
import com.loop.mobile.data.remote.network.ApiResult.Error
import com.loop.mobile.data.remote.network.ApiResult.Success
import com.loop.mobile.data.remote.services.oauth.OAuthService
import com.loop.mobile.domain.entities.AuthResult
import com.loop.mobile.domain.repositories.OAuthRepository

class OAuthRepositoryImpl(
    private val oAuthService: OAuthService,
) : OAuthRepository {
    override suspend fun oauthLogin(provider: String, code: String, redirectUri: String): ApiResult<AuthResult> {
        return when (val result = oAuthService.oauthLogin(provider, code, redirectUri)) {
            is Success -> {
                val authResult = result.data.toDomain()
                Success(authResult, result.code, result.message)
            }

            is Error -> Error(result.code, result.message, result.exception)
        }
    }
}
package com.loop.mobile.data.remote.services.oauth

import com.loop.mobile.data.remote.dto.LoginResponseDto
import com.loop.mobile.data.remote.network.ApiResult
import com.loop.mobile.data.remote.network.handleApiCall
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class OAuthServiceImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : OAuthService {
    override suspend fun oauthLogin(provider: String, code: String, redirectUri: String): ApiResult<LoginResponseDto> {
        return handleApiCall {
            httpClient.post("$baseUrl/auth/oauth-login") {
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "provider" to provider,
                        "code" to code,
                        "redirectUri" to redirectUri
                    )
                )
            }
        }
    }
}
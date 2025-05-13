package com.loop.mobile.data.remote.services.auth

import com.loop.mobile.data.remote.dto.LoginRequestDto
import com.loop.mobile.data.remote.dto.LoginResponseDto
import com.loop.mobile.data.remote.network.ApiResult
import com.loop.mobile.data.remote.network.handleApiCall
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthServiceImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : AuthService {

    override suspend fun login(email: String, password: String): ApiResult<LoginResponseDto> {
        return handleApiCall {
            httpClient.post("$baseUrl/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequestDto(email, password))
            }
        }
    }

    override suspend fun logout(): ApiResult<String> {
        return handleApiCall {
            httpClient.post("$baseUrl/auth/logout") {
                contentType(ContentType.Application.Json)
            }
        }
    }
}
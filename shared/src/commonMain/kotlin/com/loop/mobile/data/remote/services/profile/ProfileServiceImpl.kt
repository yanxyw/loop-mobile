package com.loop.mobile.data.remote.services.profile

import com.loop.mobile.data.remote.dto.GetProfileResponseDto
import com.loop.mobile.data.remote.network.ApiResult
import com.loop.mobile.data.remote.network.handleApiCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ProfileServiceImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : ProfileService {

    override suspend fun getProfile(): ApiResult<GetProfileResponseDto> {
        return handleApiCall {
            httpClient.get("$baseUrl/users/me") {
                contentType(ContentType.Application.Json)
            }
        }
    }
}
package com.loop.mobile.data.remote.network

import com.loop.mobile.data.remote.dto.LoginResponseDto
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.takeFrom
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ApiInterceptorConfig {
    var tokenProvider: suspend () -> String? = { null }
    var refreshTokenProvider: suspend () -> String? = { null }
    var baseUrl: String = ""
    var onTokensRefreshed: suspend (accessToken: String, refreshToken: String) -> Unit = { _, _ -> }
}

// Define our custom exception for retrying requests with a new response
class RetryRequestException(val response: HttpResponse) : Throwable()

// Create the plugin using the createClientPlugin API
val ApiInterceptor = createClientPlugin("ApiInterceptor", ::ApiInterceptorConfig) {
    val tokenProvider = pluginConfig.tokenProvider

    onRequest { request, _ ->
        if ("/users/" in request.url.encodedPath) {
            val token = runBlocking { tokenProvider() }
            if (token != null) {
                request.headers.append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }
}

// Extension function to install the plugin
fun HttpClientConfig<*>.installApiAuth(
    baseUrl: String,
    tokenProvider: suspend () -> String?,
    refreshTokenProvider: suspend () -> String?,
    onTokensRefreshed: suspend (accessToken: String, refreshToken: String) -> Unit
) {
    // Install our plugin for adding auth headers
    install(ApiInterceptor) {
        this.baseUrl = baseUrl
        this.tokenProvider = tokenProvider
        this.refreshTokenProvider = refreshTokenProvider
        this.onTokensRefreshed = onTokensRefreshed
    }

    // Use the HttpResponseValidator correctly
    HttpResponseValidator {
        val mutex = Mutex()

        // Handle response exceptions
        handleResponseExceptionWithRequest { exception, request ->
            // Check if it's a 401 Unauthorized error
            if (exception is ClientRequestException &&
                exception.response.status == HttpStatusCode.Unauthorized
            ) {

                // Try to refresh token by calling the refresh endpoint
                val refreshed = runBlocking {
                    mutex.withLock {
                        try {
                            // Get the refresh token
                            val refreshToken = refreshTokenProvider() ?: return@withLock false

                            // Get the client from the call property
                            val client = exception.response.call.client

                            // Call refresh endpoint using handleApiCall
                            val result: ApiResult<LoginResponseDto> = handleApiCall {
                                client.post("$baseUrl/auth/refresh") {
                                    contentType(ContentType.Application.Json)
                                    headers {
                                        append(HttpHeaders.Authorization, "Bearer $refreshToken")
                                    }
                                }
                            }

                            // Use fold to handle both success and error cases
                            result.fold(
                                onSuccess = { data ->
                                    // Store the new tokens
                                    onTokensRefreshed(data.accessToken, data.refreshToken)
                                    true
                                },
                                onFailure = { error ->
                                    // Log the error if needed
                                    println("Token refresh failed: ${error.message}")
                                    false
                                }
                            )
                        } catch (e: Exception) {
                            // If anything goes wrong during refresh, return false
                            println("Exception during token refresh: ${e.message}")
                            return@withLock false
                        }
                    }
                }

                if (refreshed) {
                    // Get new token after refresh
                    val newToken = runBlocking { tokenProvider() }

                    // Create a new request with the updated token
                    val newRequest = HttpRequestBuilder().apply {
                        takeFrom(request)
                        headers.remove(HttpHeaders.Authorization)
                        if (newToken != null) {
                            headers.append(HttpHeaders.Authorization, "Bearer $newToken")
                        }
                    }

                    // Get the client from the call property
                    val client = exception.response.call.client

                    runBlocking {
                        val newResponse = client.request(newRequest)
                        throw RetryRequestException(newResponse)
                    }
                }
            }

            // For other exceptions, just rethrow
            throw exception
        }
    }
}
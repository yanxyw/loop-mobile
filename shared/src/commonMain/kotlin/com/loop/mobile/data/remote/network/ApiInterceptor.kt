package com.loop.mobile.data.remote.network

import com.loop.mobile.data.remote.dto.LoginResponseDto
import com.loop.mobile.utils.PlatformLogger
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.takeFrom
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class ApiInterceptorConfig {
    var tokenProvider: suspend () -> String? = { null }
    var refreshTokenProvider: suspend () -> String? = { null }
    var baseUrl: String = ""
    var onTokensRefreshed: suspend (accessToken: String, refreshToken: String) -> Unit = { _, _ -> }
    var logger: PlatformLogger? = null
}

fun createAuthPlugin() = createClientPlugin("AuthPlugin", ::ApiInterceptorConfig) {
    val tokenProvider = pluginConfig.tokenProvider
    val refreshTokenProvider = pluginConfig.refreshTokenProvider
    val logger = pluginConfig.logger

    onRequest { request, _ ->
        logger?.log("Auth plugin - Request: ${request.url}")

        if (request.url.encodedPath.startsWith("/users")) {
            // Add Bearer token (Access Token) for /users requests
            request.headers.remove(HttpHeaders.Authorization)
            val accessToken = tokenProvider()
            if (accessToken != null) {
                request.headers.append(HttpHeaders.Authorization, "Bearer $accessToken")
                logger?.log("Added Bearer access token to /users request")
            }
        } else if (request.url.encodedPath.contains("/auth/logout")) {
            // Add Bearer token (Refresh Token) for /auth/logout
            request.headers.remove(HttpHeaders.Authorization)
            val refreshToken = refreshTokenProvider()
            if (refreshToken != null) {
                request.headers.append(HttpHeaders.Authorization, "Bearer $refreshToken")
                logger?.log("Added Bearer refresh token to /auth/logout request")
            }
        } else {
            logger?.log("No Bearer token added for this request")
        }
    }
}

class RetrySuccessException(val response: HttpResponse) : Exception()

fun HttpClientConfig<*>.installApiAuth(
    baseUrl: String,
    tokenProvider: suspend () -> String?,
    refreshTokenProvider: suspend () -> String?,
    onTokensRefreshed: suspend (accessToken: String, refreshToken: String) -> Unit,
    logger: PlatformLogger
) {
    // Install the auth plugin
    install(createAuthPlugin()) {
        this.baseUrl = baseUrl
        this.tokenProvider = tokenProvider
        this.refreshTokenProvider = refreshTokenProvider
        this.onTokensRefreshed = onTokensRefreshed
        this.logger = logger
    }

    // Configure response validation directly in HttpClient config
    expectSuccess = true

    // Configure HttpResponseValidator within HttpClient config
    HttpResponseValidator {
        handleResponseExceptionWithRequest { exception, request ->

            // Check if it's a 401 Unauthorized error
            if (exception is ClientRequestException &&
                exception.response.status == HttpStatusCode.Unauthorized
            ) {
                logger.log("Handling 401 Unauthorized error")

                // Skip refresh for the refresh endpoint itself
                if (request.url.encodedPath.contains("/auth/refresh")) {
                    logger.log("Refresh endpoint returned 401, not retrying")
                    throw exception
                }

                // Try to refresh token
                val refreshToken = runBlocking { refreshTokenProvider() }
                if (refreshToken == null) {
                    logger.log("No refresh token available")
                    throw exception
                }

                // Create a new client for refresh to avoid circular dependency
                val refreshClient = HttpClient {
                    install(ContentNegotiation) {
                        json(Json {
                            ignoreUnknownKeys = true
                            isLenient = true
                        })
                    }
                }

                try {
                    logger.log("Making refresh request")
                    val refreshResponse = runBlocking {
                        refreshClient.post("$baseUrl/auth/refresh") {
                            contentType(ContentType.Application.Json)
                            setBody(mapOf("refreshToken" to refreshToken))
                        }
                    }

                    if (refreshResponse.status.isSuccess()) {
                        val apiResponse: ApiResponse<LoginResponseDto> =
                            runBlocking { refreshResponse.body() }
                        val data = apiResponse.data

                        if (data != null) {
                            runBlocking { onTokensRefreshed(data.accessToken, data.refreshToken) }
                            logger.log("Tokens refreshed successfully")

                            // Retry the original request with new token
                            val newToken = runBlocking { tokenProvider() }
                            if (newToken != null) {
                                logger.log("Retrying the original request with new token")

                                // Create a fresh client to retry the request
                                val retryClient = HttpClient {
                                    install(ContentNegotiation) {
                                        json(Json {
                                            ignoreUnknownKeys = true
                                            isLenient = true
                                        })
                                    }
                                }

                                val retryResponse = runBlocking {
                                    retryClient.request {
                                        takeFrom(request)
                                        headers.remove(HttpHeaders.Authorization)
                                        headers.append(
                                            HttpHeaders.Authorization,
                                            "Bearer $newToken"
                                        )
                                    }
                                }

                                if (retryResponse.status.isSuccess()) {
                                    logger.log("Retried request successful: ${retryResponse.status}")
                                    throw RetrySuccessException(retryResponse)
                                } else {
                                    logger.log("Retried request failed: ${retryResponse.status}")
                                    throw exception
                                }
                            } else {
                                logger.log("New token is null, cannot retry")
                                throw exception
                            }
                        } else {
                            logger.log("Refresh response data is null")
                            throw exception
                        }
                    } else {
                        logger.log("Refresh failed with status: ${refreshResponse.status}")
                        throw exception
                    }
                } catch (e: RetrySuccessException) {
                    logger.log("Returning successful retry response")
                    throw e // This will be caught and the response used directly
                } catch (e: Exception) {
                    logger.log("Error during token refresh: ${e.message}")
                    throw exception
                } finally {
                    refreshClient.close()
                }
            }

            throw exception  // For other types of exceptions, rethrow
        }
    }
}

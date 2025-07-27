package com.loop.mobile.data.remote.network

import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse

suspend inline fun <reified T> HttpResponse.toApiResult(): ApiResult<T> {
    return try {
        val wrapper = this.body<ApiResponse<T>>()

        if (wrapper.status == "SUCCESS") {
            ApiResult.Success(wrapper.code, wrapper.message, wrapper.data)
        } else {
            ApiResult.Error(wrapper.code, wrapper.message)
        }
    } catch (e: Exception) {
        ApiResult.Error(
            code = this.status.value,
            message = "Failed to parse response: ${e.message}",
            exception = e
        )
    }
}


suspend inline fun <reified T> handleApiCall(
    crossinline call: suspend () -> HttpResponse
): ApiResult<T> {
    return try {
        val response = call()
        response.toApiResult()
    } catch (e: RetrySuccessException) {
        e.response.toApiResult<T>()
    } catch (e: ResponseException) {
        try {
            val errorWrapper = e.response.body<ApiResponse<T>>()
            ApiResult.Error(
                code = errorWrapper.code,
                message = errorWrapper.message,
                exception = e
            )
        } catch (_: Exception) {
            ApiResult.Error(
                code = e.response.status.value,
                message = "HTTP ${e.response.status.value}: ${e.message}",
                exception = e
            )
        }
    } catch (e: Exception) {
        ApiResult.Error(
            code = -1,
            message = "Unexpected error: ${e.message}",
            exception = e
        )
    }
}

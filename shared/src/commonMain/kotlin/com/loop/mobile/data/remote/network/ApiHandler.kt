package com.loop.mobile.data.remote.network

import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse

suspend inline fun <reified T> HttpResponse.toApiResult(): ApiResult<T> {
    return try {
        val wrapper = this.body<ApiResponse<T>>()

        if (wrapper.status == "SUCCESS" && wrapper.data != null) {
            ApiResult.Success(wrapper.data, wrapper.code, wrapper.message)
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
    } catch (e: ResponseException) {
        ApiResult.Error(
            code = e.response.status.value,
            message = "HTTP error: ${e.message}",
            exception = e
        )
    } catch (e: Exception) {
        ApiResult.Error(
            code = -1,
            message = "Unexpected error: ${e.message}",
            exception = e
        )
    }
}

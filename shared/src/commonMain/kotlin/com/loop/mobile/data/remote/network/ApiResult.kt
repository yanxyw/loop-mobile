package com.loop.mobile.data.remote.network

sealed class ApiResult<out T> {
    data class Success<T>(val data: T, val code: Int, val message: String) : ApiResult<T>()
    data class Error(val code: Int, val message: String, val exception: Throwable? = null) :
        ApiResult<Nothing>()

    inline fun <R> fold(
        onSuccess: (T) -> R,
        onFailure: (Error) -> R
    ): R = when (this) {
        is Success -> onSuccess(data)
        is Error -> onFailure(this)
    }
}
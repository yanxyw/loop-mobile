package com.loop.mobile.presentation.auth.logout

data class LogoutState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val message: String? = null
)
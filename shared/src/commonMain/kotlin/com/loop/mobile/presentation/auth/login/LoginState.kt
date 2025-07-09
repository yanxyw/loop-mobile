package com.loop.mobile.presentation.auth.login

data class LoginState(
    val email: String = "",
    val password: String = "",
    val emailTouched: Boolean = false,
    val passwordTouched: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val loadingProvider: String? = null,
    val error: String? = null,
    val isSuccess: Boolean = false
)
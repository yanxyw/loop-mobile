package com.loop.mobile.presentation.auth.signup

data class SignUpState(
    val email: String = "",
    val password: String = "",
    val username: String = "",

    val emailTouched: Boolean = false,
    val passwordTouched: Boolean = false,
    val usernameTouched: Boolean = false,

    val emailError: String? = null,
    val passwordError: String? = null,
    val usernameError: String? = null,

    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
package com.loop.mobile.presentation.auth.login

sealed interface LoginAction {
    data class OnEmailChange(val email: String) : LoginAction
    data object OnEmailBlur : LoginAction
    data class OnPasswordChange(val password: String) : LoginAction
    data object OnPasswordBlur : LoginAction
    data object OnLogin : LoginAction
    data class OnOAuthLogin(
        val provider: String,
        val code: String,
        val redirectUri: String
    ) : LoginAction
}
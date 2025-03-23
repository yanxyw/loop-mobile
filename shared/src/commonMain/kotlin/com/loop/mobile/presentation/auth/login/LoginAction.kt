package com.loop.mobile.presentation.auth.login

sealed interface LoginAction {
    data class OnEmailChange(val email: String) : LoginAction
    data class OnPasswordChange(val password: String) : LoginAction
    data object OnLogin : LoginAction
}
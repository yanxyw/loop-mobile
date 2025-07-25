package com.loop.mobile.presentation.auth.signup

sealed interface SignUpAction {
    data class OnEmailChange(val email: String) : SignUpAction
    data object OnEmailBlur : SignUpAction

    data class OnPasswordChange(val password: String) : SignUpAction
    data object OnPasswordBlur : SignUpAction

    data class OnUsernameChange(val username: String) : SignUpAction
    data object OnUsernameBlur : SignUpAction

    data object OnSignUp : SignUpAction
}
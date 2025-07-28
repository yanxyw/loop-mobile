package com.loop.mobile.domain.validation

object AuthInputValidator {

    private val emailRules = listOf(notEmpty, validEmailFormat)
    private val usernameRules = listOf(notEmpty, minLength(3), maxLength(20))

    fun validateEmail(input: String): String? =
        validateInput(input, emailRules)

    fun validateUsername(input: String): String? =
        validateInput(input, usernameRules)

    fun validatePassword(input: String): String? =
        passwordValidationError(input)
}
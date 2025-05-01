package com.loop.mobile.domain.validation

object AuthInputValidator {

    private val emailRules = listOf(notEmpty, validEmailFormat)
    private val passwordRules = listOf(notEmpty, minLength(8))
    private val usernameRules = listOf(notEmpty, minLength(3), maxLength(20))

    fun validateEmail(input: String): String? =
        validateInput(input, emailRules)

    fun validatePassword(input: String): String? =
        validateInput(input, passwordRules)

    fun validateUsername(input: String): String? =
        validateInput(input, usernameRules)
}
package com.loop.mobile.domain.validation

data class PasswordRequirement(
    val label: String,
    val errorMessage: String,
    val isSatisfied: Boolean
)

fun getPasswordRequirements(password: String): List<PasswordRequirement> = listOf(
    PasswordRequirement(
        label = "At least 8 characters",
        errorMessage = "Must be at least 8 characters long",
        isSatisfied = password.length >= 8
    ),
    PasswordRequirement(
        label = "A number",
        errorMessage = "Must include at least one number",
        isSatisfied = password.any { it.isDigit() }
    ),
    PasswordRequirement(
        label = "A lowercase letter",
        errorMessage = "Must include at least one lowercase letter",
        isSatisfied = password.any { it.isLowerCase() }
    ),
    PasswordRequirement(
        label = "An uppercase letter",
        errorMessage = "Must include at least one uppercase letter",
        isSatisfied = password.any { it.isUpperCase() }
    )
)

fun passwordValidationError(password: String): String? {
    if (password.isBlank()) return "Password cannot be empty"
    return getPasswordRequirements(password)
        .firstOrNull { !it.isSatisfied }
        ?.errorMessage
}

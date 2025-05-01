package com.loop.mobile.domain.validation

val notEmpty: Rule<String> = { input ->
    if (input.isBlank()) "Field cannot be empty" else null
}

val validEmailFormat: Rule<String> = { input ->
    if (!input.contains("@") || !input.contains(".")) "Invalid email address" else null
}

fun minLength(length: Int): Rule<String> = { input ->
    if (input.length < length) "Must be at least $length characters" else null
}

fun maxLength(length: Int): Rule<String> = { input ->
    if (input.length > length) "Must be no more than $length characters" else null
}
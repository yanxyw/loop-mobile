package com.loop.mobile.domain.validation

typealias Rule<T> = (T) -> String?

fun <T> validateInput(input: T, rules: List<Rule<T>>): String? {
    return rules.firstNotNullOfOrNull { it(input) }
}
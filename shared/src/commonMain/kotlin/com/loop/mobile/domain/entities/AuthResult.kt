package com.loop.mobile.domain.entities

data class AuthResult(
    val userId: Long,
    val accessToken: String,
    val refreshToken: String
)
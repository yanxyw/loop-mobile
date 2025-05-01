package com.loop.mobile.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    val userId: Long,
    val accessToken: String,
    val refreshToken: String
)
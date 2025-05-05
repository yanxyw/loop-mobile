package com.loop.mobile.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GetProfileResponseDto(
    val id: Long,
    val email: String,
    val username: String,
    val admin: Boolean,
    val profileUrl: String? = null
)
package com.loop.mobile.domain.entities

data class DecodedUser(
    val userId: Long,
    val email: String,
    val username: String,
    val isAdmin: Boolean,
    val profileUrl: String? = null
)
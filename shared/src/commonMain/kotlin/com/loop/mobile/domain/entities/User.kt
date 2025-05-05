package com.loop.mobile.domain.entities

data class User(
    val id: Long,
    val email: String,
    val username: String,
    val admin: Boolean,
    val profileUrl: String? = null
)
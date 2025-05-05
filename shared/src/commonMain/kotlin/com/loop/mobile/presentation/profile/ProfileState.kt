package com.loop.mobile.presentation.profile

import com.loop.mobile.domain.entities.User

data class ProfileState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null
)
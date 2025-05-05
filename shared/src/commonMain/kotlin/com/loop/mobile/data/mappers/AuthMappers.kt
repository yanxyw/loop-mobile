package com.loop.mobile.data.mappers

import com.loop.mobile.data.remote.dto.LoginResponseDto
import com.loop.mobile.domain.entities.AuthResult

fun LoginResponseDto.toDomain(): AuthResult {
    return AuthResult(
        userId = this.userId,
        accessToken = this.accessToken,
        refreshToken = this.refreshToken
    )
}
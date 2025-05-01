package com.loop.mobile.data.mappers

import com.loop.mobile.data.remote.dto.LoginResponseDto
import com.loop.mobile.domain.entities.AuthResult

class AuthMapper {
    fun mapLoginResponseToDomain(dto: LoginResponseDto): AuthResult {
        return AuthResult(
            userId = dto.userId,
            accessToken = dto.accessToken,
            refreshToken = dto.refreshToken
        )
    }
}
package com.loop.mobile.data.mappers

import com.loop.mobile.data.remote.dto.GetProfileResponseDto

import com.loop.mobile.domain.entities.User

fun GetProfileResponseDto.toDomain(): User {
    return User(
        id = this.id,
        email = this.email,
        username = this.username,
        admin = this.admin,
        profileUrl = this.profileUrl
    )
}
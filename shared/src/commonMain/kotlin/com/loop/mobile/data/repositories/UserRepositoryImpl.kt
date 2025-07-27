package com.loop.mobile.data.repositories

import com.loop.mobile.data.mappers.toDomain
import com.loop.mobile.data.remote.network.ApiResult
import com.loop.mobile.data.remote.network.ApiResult.Error
import com.loop.mobile.data.remote.network.ApiResult.Success
import com.loop.mobile.data.remote.services.profile.ProfileService
import com.loop.mobile.domain.entities.User
import com.loop.mobile.domain.repositories.UserRepository

class UserRepositoryImpl(
    private val profileService: ProfileService,
) : UserRepository {

    override suspend fun getMe(): ApiResult<User> {
        return when (val result = profileService.getProfile()) {
            is Success -> {
                val user = result.data?.toDomain()
                Success(result.code, result.message, user)
            }

            is Error -> {
                Error(result.code, result.message, result.exception)
            }
        }
    }
}
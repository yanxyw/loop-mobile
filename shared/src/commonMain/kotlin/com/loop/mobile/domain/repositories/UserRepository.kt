package com.loop.mobile.domain.repositories

import com.loop.mobile.data.remote.network.ApiResult
import com.loop.mobile.domain.entities.User

interface UserRepository {
    suspend fun getMe(): ApiResult<User>
}

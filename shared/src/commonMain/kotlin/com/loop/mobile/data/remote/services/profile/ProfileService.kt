package com.loop.mobile.data.remote.services.profile

import com.loop.mobile.data.remote.dto.GetProfileResponseDto
import com.loop.mobile.data.remote.network.ApiResult

interface ProfileService {
    suspend fun getProfile(): ApiResult<GetProfileResponseDto>
}
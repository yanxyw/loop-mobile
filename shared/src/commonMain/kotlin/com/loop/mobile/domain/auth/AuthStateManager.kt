package com.loop.mobile.domain.auth

import com.loop.mobile.domain.entities.DecodedUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AuthStateManager {
    private val _user = MutableStateFlow<DecodedUser?>(null)
    val user: StateFlow<DecodedUser?> = _user

    val isLoggedIn: StateFlow<Boolean> = _user.map { it != null }.stateIn(
        scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
        started = SharingStarted.Eagerly,
        initialValue = false
    )

    fun setUser(decodedUser: DecodedUser?) {
        _user.value = decodedUser
    }

    fun clearUser() {
        _user.value = null
    }
}
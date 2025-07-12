package com.loop.mobile.domain.auth

import com.loop.mobile.domain.entities.DecodedUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthStateManager {
    private val _user = MutableStateFlow<DecodedUser?>(null)
    val user: StateFlow<DecodedUser?> = _user

    private val _provider = MutableStateFlow<String?>(null)
    val provider: StateFlow<String?> = _provider

    val isLoggedIn: StateFlow<Boolean> = _user.map { it != null }.stateIn(
        scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
        started = SharingStarted.Eagerly,
        initialValue = false
    )

    fun observeUser(callback: (DecodedUser?) -> Unit): Job {
        val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        return scope.launch {
            user.collect {
                callback(it)
            }
        }
    }

    fun setUser(decodedUser: DecodedUser?, provider: String? = null) {
        _user.value = decodedUser
        if (provider != null) {
            _provider.value = provider
        }
    }

    fun clearUser() {
        _user.value = null
        _provider.value = null
    }
}
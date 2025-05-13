package com.loop.mobile.presentation.auth.logout

import com.loop.mobile.domain.repositories.AuthRepository
import com.loop.mobile.presentation.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LogoutViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    private val _state = MutableStateFlow(LogoutState())
    val state: StateFlow<LogoutState> = _state

    fun logout() {
        _state.value = LogoutState(isLoading = true)

        scope.launch {
            val result = authRepository.logout()
            result.fold(
                onSuccess = { message ->
                    _state.value = LogoutState(isSuccess = true, message = message)
                },
                onFailure = {
                    _state.value = LogoutState(error = it.message)
                }
            )
        }
    }
}


package com.loop.mobile.presentation.auth.login

import com.loop.mobile.domain.repositories.AuthRepository
import com.loop.mobile.presentation.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    fun onIntent(intent: LoginAction) {
        when (intent) {
            is LoginAction.OnEmailChange -> {
                _state.update { it.copy(email = intent.email, error = null) }
            }

            is LoginAction.OnPasswordChange -> {
                _state.update { it.copy(password = intent.password, error = null) }
            }

            LoginAction.OnLogin -> {
                if (_state.value.email.isBlank() || _state.value.password.isBlank()) {
                    _state.update { it.copy(error = "Email and password cannot be empty") }
                    return
                }

                _state.update { it.copy(isLoading = true, error = null) }

                scope.launch {
                    val result = authRepository.login(
                        _state.value.email,
                        _state.value.password
                    )

                    result.fold(
                        onSuccess = { authResult ->
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    isSuccess = true
                                )
                            }
                        },
                        onFailure = { error ->
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = error.message
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    fun observeState(callback: (LoginState) -> Unit) {
        scope.launch {
            state.collect { callback(it) }
        }
    }
}
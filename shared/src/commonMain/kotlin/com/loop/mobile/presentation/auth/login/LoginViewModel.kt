package com.loop.mobile.presentation.auth.login

import com.loop.mobile.domain.usecases.LoginUseCase
import com.loop.mobile.domain.validation.AuthInputValidator
import com.loop.mobile.presentation.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    fun onIntent(action: LoginAction) {
        when (action) {
            is LoginAction.OnEmailChange -> {
                _state.update {
                    it.copy(
                        email = action.email,
                        emailError = null,
                        error = null
                    )
                }
            }

            is LoginAction.OnEmailBlur -> {
                val error = AuthInputValidator.validateEmail(_state.value.email)
                _state.update {
                    it.copy(
                        emailTouched = true,
                        emailError = error
                    )
                }
            }

            is LoginAction.OnPasswordChange -> {
                _state.update {
                    it.copy(
                        password = action.password,
                        passwordError = null,
                        error = null
                    )
                }
            }

            is LoginAction.OnPasswordBlur -> {
                val error = AuthInputValidator.validatePassword(_state.value.password)
                _state.update {
                    it.copy(
                        passwordTouched = true,
                        passwordError = error
                    )
                }
            }

            LoginAction.OnLogin -> {
                val email = _state.value.email
                val password = _state.value.password

                val emailError = AuthInputValidator.validateEmail(email)
                val passwordError = AuthInputValidator.validatePassword(password)

                if (emailError != null || passwordError != null) {
                    _state.update {
                        it.copy(
                            emailTouched = true,
                            passwordTouched = true,
                            emailError = emailError,
                            passwordError = passwordError
                        )
                    }
                    return
                }

                _state.update { it.copy(isLoading = true, error = null) }

                scope.launch {
                    val result = loginUseCase(email, password)

                    result.fold(
                        onSuccess = {
                            _state.update {
                                it.copy(isLoading = false, isSuccess = true)
                            }
                        },
                        onFailure = { err ->
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = err.message
                                )
                            }
                        }
                    )
                }
            }

            is LoginAction.OnOAuthLogin -> {
                _state.update { it.copy(isLoading = true, error = null) }

                scope.launch {
                    val result = loginUseCase(
                        provider = action.provider,
                        code = action.code,
                        redirectUri = action.redirectUri
                    )

                    result.fold(
                        onSuccess = {
                            _state.update { it.copy(isLoading = false, isSuccess = true) }
                        },
                        onFailure = { err ->
                            _state.update {
                                it.copy(isLoading = false, error = err.message)
                            }
                        }
                    )
                }
            }
        }
    }

    fun clearState() {
        _state.value = LoginState()
    }

    fun observeState(callback: (LoginState) -> Unit) {
        scope.launch {
            state.collect(callback)
        }
    }
}
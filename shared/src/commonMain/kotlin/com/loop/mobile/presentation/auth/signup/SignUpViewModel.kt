package com.loop.mobile.presentation.auth.signup

import com.loop.mobile.domain.repositories.AuthRepository
import com.loop.mobile.domain.validation.AuthInputValidator
import com.loop.mobile.presentation.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state: StateFlow<SignUpState> = _state

    fun onIntent(action: SignUpAction) {
        when (action) {
            is SignUpAction.OnEmailChange -> {
                _state.update {
                    it.copy(email = action.email, emailError = null, error = null)
                }
            }

            SignUpAction.OnEmailBlur -> {
                val error = AuthInputValidator.validateEmail(_state.value.email)
                _state.update {
                    it.copy(emailTouched = true, emailError = error)
                }
            }

            is SignUpAction.OnPasswordChange -> {
                _state.update {
                    it.copy(password = action.password, passwordError = null, error = null)
                }
            }

            SignUpAction.OnPasswordBlur -> {
                val error = AuthInputValidator.validatePassword(_state.value.password)
                _state.update {
                    it.copy(passwordTouched = true, passwordError = error)
                }
            }

            is SignUpAction.OnUsernameChange -> {
                _state.update {
                    it.copy(username = action.username, usernameError = null, error = null)
                }
            }

            SignUpAction.OnUsernameBlur -> {
                val error = AuthInputValidator.validateUsername(_state.value.username)
                _state.update {
                    it.copy(usernameTouched = true, usernameError = error)
                }
            }

            SignUpAction.OnSignUp -> {
                val email = _state.value.email
                val password = _state.value.password
                val username = _state.value.username

                val emailError = AuthInputValidator.validateEmail(email)
                val passwordError = AuthInputValidator.validatePassword(password)
                val usernameError = AuthInputValidator.validateUsername(username)

                if (emailError != null || passwordError != null || usernameError != null) {
                    _state.update {
                        it.copy(
                            emailTouched = true,
                            passwordTouched = true,
                            usernameTouched = true,
                            emailError = emailError,
                            passwordError = passwordError,
                            usernameError = usernameError
                        )
                    }
                    return
                }

                _state.update { it.copy(isLoading = true, error = null) }

                scope.launch {
                    val result = authRepository.signUp(email, password, username)

                    result.fold(
                        onSuccess = {
                            _state.update {
                                it.copy(isLoading = false, isSuccess = true)
                            }
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
        _state.value = SignUpState()
    }

    fun observeState(callback: (SignUpState) -> Unit) {
        scope.launch {
            state.collect(callback)
        }
    }
}

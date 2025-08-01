package com.loop.mobile.presentation.auth.signup

import com.loop.mobile.domain.repositories.AuthRepository
import com.loop.mobile.domain.validation.AuthInputValidator
import com.loop.mobile.domain.validation.PasswordRequirement
import com.loop.mobile.domain.validation.getPasswordRequirements
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
    val passwordRequirements: List<PasswordRequirement>
        get() = getPasswordRequirements(state.value.password)

    fun onIntent(action: SignUpAction) {
        when (action) {
            is SignUpAction.OnEmailChange -> {
                _state.update {
                    it.copy(email = action.email, emailError = null, error = null)
                }
            }

            SignUpAction.OnEmailBlur -> {
                val email = _state.value.email
                val error = AuthInputValidator.validateEmail(email)

                _state.update { it.copy(emailTouched = true, emailError = error, error = null) }

                // If email format is valid, check with server
                if (error == null) {
                    scope.launch {
                        val result = authRepository.checkEmail(email)

                        result.fold(
                            onSuccess = {
                                _state.update {
                                    it.copy(emailError = null)
                                }
                            },
                            onFailure = { err ->
                                _state.update {
                                    it.copy(emailError = err.message)
                                }
                            }
                        )
                    }
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

            SignUpAction.PreviousStep -> {
                if (_state.value.step > 0) {
                    _state.update { it.copy(step = it.step - 1) }
                }
            }

            SignUpAction.NextStep -> {
                when (_state.value.step) {
                    0 -> {
                        val email = _state.value.email
                        val formatError = AuthInputValidator.validateEmail(email)

                        if (formatError != null) {
                            _state.update {
                                it.copy(emailTouched = true, emailError = formatError)
                            }
                            return
                        }

                        scope.launch {
                            val result = authRepository.checkEmail(email)
                            result.fold(
                                onSuccess = {
                                    _state.update { it.copy(step = _state.value.step + 1, emailError = null) }
                                },
                                onFailure = { err ->
                                    _state.update {
                                        it.copy(emailTouched = true, emailError = err.message)
                                    }
                                }
                            )
                        }

                        return
                    }

                    1 -> {
                        val passwordError = AuthInputValidator.validatePassword(_state.value.password)
                        if (passwordError != null) {
                            _state.update {
                                it.copy(passwordTouched = true, passwordError = passwordError)
                            }
                            return
                        }

                        if (_state.value.step < 2) {
                            _state.update { it.copy(step = _state.value.step + 1) }
                        }
                    }
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

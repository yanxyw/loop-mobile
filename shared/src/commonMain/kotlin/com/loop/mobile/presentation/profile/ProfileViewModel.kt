package com.loop.mobile.presentation.profile

import com.loop.mobile.data.mappers.toDecodedUser
import com.loop.mobile.domain.auth.AuthStateManager
import com.loop.mobile.domain.repositories.UserRepository
import com.loop.mobile.presentation.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val authStateManager: AuthStateManager
) : BaseViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state

    fun onIntent(action: ProfileAction) {
        when (action) {
            ProfileAction.LoadProfile -> loadProfile()
        }
    }

    private fun loadProfile() {
        _state.update { it.copy(isLoading = true, error = null) }
        showLoading()

        scope.launch {
            val result = userRepository.getMe()

            result.fold(
                onSuccess = { user ->
                    _state.update {
                        it.copy(isLoading = false, user = user)

                    }
                    authStateManager.setUser(user?.toDecodedUser())
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(isLoading = false, error = error.message)
                    }
                    showDialog("Error", error.message)
                }
            )

            hideLoading()
        }
    }

    fun clearProfile() {
        _state.value = ProfileState()
    }

    fun observeState(callback: (ProfileState) -> Unit) {
        scope.launch {
            state.collect(callback)
        }
    }
}

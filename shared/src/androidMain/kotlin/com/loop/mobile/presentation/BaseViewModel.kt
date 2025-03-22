package com.loop.mobile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope

actual open class BaseViewModel : ViewModel() {
    actual var scope: CoroutineScope = viewModelScope

    public override fun onCleared() {
        super.onCleared()
    }

    actual fun showLoading() {
        // Android-specific implementation
    }

    actual fun hideLoading() {
        // Android-specific implementation
    }

    actual fun showDialog(title: String, message: String) {
        // Android-specific implementation
    }

    actual fun hideDialog() {
        // Android-specific implementation
    }
}
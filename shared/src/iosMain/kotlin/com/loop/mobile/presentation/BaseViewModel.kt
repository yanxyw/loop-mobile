package com.loop.mobile.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

actual open class BaseViewModel {
    actual var scope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    fun clear() {
        scope.cancel()
    }

    fun start() {
        scope = CoroutineScope(Dispatchers.Main)
    }

    actual fun showLoading() {
        // iOS-specific implementation
    }

    actual fun hideLoading() {
        // iOS-specific implementation
    }

    actual fun showDialog(title: String, message: String) {
        // iOS-specific implementation
    }

    actual fun hideDialog() {
        // iOS-specific implementation
    }
}
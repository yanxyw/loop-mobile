package com.loop.mobile.utils

import android.util.Log

// Actual implementation for Android
actual class PlatformLogger {
    actual fun log(message: String) {
        Log.d("ApiInterceptor", message)
    }
}
package com.loop.mobile.utils

import platform.Foundation.NSLog

actual class PlatformLogger {
    actual fun log(message: String) {
        NSLog(message)
    }
}
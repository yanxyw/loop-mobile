package com.loop.mobile.domain.utils

actual fun getCurrentTimeInSeconds(): Long {
    return System.currentTimeMillis() / 1000
}
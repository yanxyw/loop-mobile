package com.loop.mobile.domain.utils

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual fun getCurrentTimeInSeconds(): Long {
    return (NSDate().timeIntervalSince1970).toLong()
}
package com.loop.mobile.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

fun initKoin(appContext: (KoinApplication.() -> Unit)? = null) {
    startKoin {
        appContext?.invoke(this)
        modules(commonModules())
    }
}
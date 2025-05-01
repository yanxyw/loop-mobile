package com.loop.mobile.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

fun initKoin(
    appContext: (KoinApplication.() -> Unit)? = null,
    baseUrl: String = "http://localhost:8080/api/v1"
) {
    startKoin {
        appContext?.invoke(this)
        modules(commonModules())
        properties(mapOf("baseUrl" to baseUrl))
    }
}
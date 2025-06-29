package com.loop.mobile.di

import com.loop.mobile.presentation.theme.ThemeManager
import com.loop.mobile.presentation.theme.ThemeStorage
import com.loop.mobile.presentation.theme.provideThemeStorage
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initKoin(
    appContext: (KoinApplication.() -> Unit)? = null,
    baseUrl: String = "http://10.0.2.2:8080/api/v1"
) {
    startKoin {
        appContext?.invoke(this)
        modules(commonModules() + themeModule)
        properties(mapOf("baseUrl" to baseUrl))
    }
}

val themeModule = module {
    single<ThemeStorage> { provideThemeStorage(androidContext()) }
    single { ThemeManager(androidContext(), get()) }
}
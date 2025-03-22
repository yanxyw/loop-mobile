package com.loop.mobile.di

import org.koin.core.context.startKoin

fun initKoinAndroid() {
    startKoin {
        modules(commonModules())
    }
}
package com.loop.mobile.di

import com.loop.mobile.presentation.search.SearchViewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

val sharedModule = module {
    factory { SearchViewModel() }
}

fun initKoin() {
    startKoin {
        modules(sharedModule)
    }
}
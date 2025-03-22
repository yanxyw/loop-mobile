package com.loop.mobile.di

import com.loop.mobile.presentation.search.SearchViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val commonModule = module {
    single { SearchViewModel() }
}

fun commonModules(): List<Module> {
    return listOf(commonModule)
}
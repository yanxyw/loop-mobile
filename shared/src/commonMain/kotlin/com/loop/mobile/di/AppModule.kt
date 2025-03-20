package com.loop.mobile.di

import com.loop.mobile.presentation.search.SearchViewModel
import org.koin.dsl.module

val appModule = module {
    single { SearchViewModel() }
}
package com.loop.mobile.di

import com.loop.mobile.presentation.search.SearchViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

@Suppress("unused")
fun initKoin() {
    startKoin {
        modules(commonModules())
    }
}

@Suppress("unused")
class SearchViewModelInjector : KoinComponent {
    val searchViewModel: SearchViewModel by inject()
}
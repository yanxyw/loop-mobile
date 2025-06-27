package com.loop.mobile.di

import com.loop.mobile.domain.auth.AuthStateManager
import com.loop.mobile.presentation.auth.login.LoginViewModel
import com.loop.mobile.presentation.search.SearchViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

@Suppress("unused")
fun initKoin() {
    startKoin {
        properties(mapOf("baseUrl" to "http://localhost:8080/api/v1"))
        modules(commonModules())
    }
}

@Suppress("unused")
class SearchViewModelInjector : KoinComponent {
    val searchViewModel: SearchViewModel by inject()
}

@Suppress("unused")
class LoginViewModelInjector : KoinComponent {
    val loginViewModel: LoginViewModel by inject()
}

@Suppress("unused")
class AuthStateManagerInjector : KoinComponent {
    val authStateManager: AuthStateManager by inject()
}
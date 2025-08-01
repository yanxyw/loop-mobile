package com.loop.mobile.di

import com.loop.mobile.data.local.SessionStorage
import com.loop.mobile.domain.auth.AuthStateManager
import com.loop.mobile.domain.repositories.UserRepository
import com.loop.mobile.presentation.auth.login.LoginViewModel
import com.loop.mobile.presentation.auth.logout.LogoutViewModel
import com.loop.mobile.presentation.profile.ProfileViewModel
import com.loop.mobile.presentation.search.SearchViewModel
import com.loop.mobile.presentation.theme.ThemeManager
import com.loop.mobile.presentation.theme.ThemeStorage
import com.loop.mobile.presentation.theme.provideThemeStorage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module
import platform.UIKit.UIApplication

@Suppress("unused")
fun initKoin() {
    startKoin {
        properties(mapOf("baseUrl" to "https://proper-cricket-wholly.ngrok-free.app/api/v1"))
        modules(commonModules() + themeModule)
    }
}

val themeModule = module {
    single<ThemeStorage> { provideThemeStorage(UIApplication.sharedApplication()) }
    single { ThemeManager(UIApplication.sharedApplication(), get()) }
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


@Suppress("unused")
class SessionStorageInjector : KoinComponent {
    val sessionStorage: SessionStorage by inject()
}

@Suppress("unused")
class UserRepositoryInjector : KoinComponent {
    val userRepository: UserRepository by inject()
}

@Suppress("unused")
class ThemeStorageInjector : KoinComponent {
    val themeStorage: ThemeStorage by inject()
}

@Suppress("unused")
class ThemeManagerInjector : KoinComponent {
    val themeManager: ThemeManager by inject()
}

@Suppress("unused")
class ProfileViewModelInjector : KoinComponent {
    val profileViewModel: ProfileViewModel by inject()
}

@Suppress("unused")
class LogoutViewModelInjector : KoinComponent {
    val logoutViewModel: LogoutViewModel by inject()
}
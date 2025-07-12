package com.loop.mobile.di

import com.loop.mobile.data.local.SessionStorage
import com.loop.mobile.data.local.provideSessionStorage
import com.loop.mobile.data.remote.network.installApiAuth
import com.loop.mobile.data.remote.services.auth.AuthService
import com.loop.mobile.data.remote.services.auth.AuthServiceImpl
import com.loop.mobile.data.remote.services.oauth.OAuthService
import com.loop.mobile.data.remote.services.oauth.OAuthServiceImpl
import com.loop.mobile.data.remote.services.profile.ProfileService
import com.loop.mobile.data.remote.services.profile.ProfileServiceImpl
import com.loop.mobile.data.repositories.AuthRepositoryImpl
import com.loop.mobile.data.repositories.OAuthRepositoryImpl
import com.loop.mobile.data.repositories.UserRepositoryImpl
import com.loop.mobile.domain.auth.AuthStateManager
import com.loop.mobile.domain.repositories.AuthRepository
import com.loop.mobile.domain.repositories.OAuthRepository
import com.loop.mobile.domain.repositories.UserRepository
import com.loop.mobile.domain.usecases.LoginUseCase
import com.loop.mobile.presentation.auth.login.LoginViewModel
import com.loop.mobile.presentation.auth.logout.LogoutViewModel
import com.loop.mobile.presentation.profile.ProfileViewModel
import com.loop.mobile.presentation.search.SearchViewModel
import com.loop.mobile.utils.PlatformLogger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient {
            // Get token storage from DI
            val sessionStorage = get<SessionStorage>()
            val platformLogger: PlatformLogger = get()
            platformLogger.log("Creating HttpClient")

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        platformLogger.log("HTTP: $message")
                    }
                }
                level = LogLevel.ALL
            }

            installApiAuth(
                baseUrl = get<String>(named("baseUrl")),
                tokenProvider = { sessionStorage.getAccessToken() },
                refreshTokenProvider = { sessionStorage.getRefreshToken() },
                onTokensRefreshed = { accessToken, refreshToken ->
                    sessionStorage.saveTokens(accessToken, refreshToken)
                    platformLogger.log("Tokens refreshed: $accessToken")
                },
                logger = platformLogger
            )
        }
    }

    // Base URL as a simple string property
    single<String>(qualifier = named("baseUrl")) { getProperty("baseUrl") }

    // API Services - use the named qualifier to get the baseUrl
    single<AuthService> { AuthServiceImpl(get(), get(named("baseUrl"))) }
    single<OAuthService> { OAuthServiceImpl(get(), get(named("baseUrl"))) }
    single<ProfileService> { ProfileServiceImpl(get(), get(named("baseUrl"))) }
}

val repositoriesModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
    single<OAuthRepository> { OAuthRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
}

val viewModelModule = module {
    factory { SearchViewModel() }
    factory { LoginViewModel(get()) }
    factory { LogoutViewModel(get()) }
    factory { ProfileViewModel(get(), get()) }
}

val useCaseModule = module {
    single {
        LoginUseCase(
            authRepository = get(),
            oauthRepository = get(),
            sessionStorage = get(),
            authStateManager = get()
        )
    }
}

val storageModule = module {
    single<SessionStorage> { provideSessionStorage() }
    single { AuthStateManager() }
}

val loggingModule = module {
    single { PlatformLogger() }
}

fun commonModules(): List<Module> {
    return listOf(
        networkModule,
        repositoriesModule,
        storageModule,
        useCaseModule,
        viewModelModule,
        loggingModule,
    )
}
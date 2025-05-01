package com.loop.mobile.di

import com.loop.mobile.data.mappers.AuthMapper
import com.loop.mobile.data.remote.services.AuthService
import com.loop.mobile.data.remote.services.AuthServiceImpl
import com.loop.mobile.data.repositories.AuthRepositoryImpl
import com.loop.mobile.domain.repositories.AuthRepository
import com.loop.mobile.presentation.auth.login.LoginViewModel
import com.loop.mobile.presentation.search.SearchViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

// Network module for HTTP client and API services
val networkModule = module {
    // HTTP Client with JSON serialization support
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.HEADERS
            }
        }
    }

    // Base URL as a simple string property
    single<String>(qualifier = named("baseUrl")) { getProperty("baseUrl") }

    // API Services - use the named qualifier to get the baseUrl
    single<AuthService> { AuthServiceImpl(get(), get(named("baseUrl"))) }
}

// Mappers module
val mapperModule = module {
    single { AuthMapper() }
}

// Repositories module
val repositoriesModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
}

// ViewModels module
val viewModelModule = module {
    factory { SearchViewModel() }
    factory { LoginViewModel(get()) } // Update to inject AuthRepository
}

fun commonModules(): List<Module> {
    return listOf(
        networkModule,
        mapperModule,
        repositoriesModule,
        viewModelModule
    )
}
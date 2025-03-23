package com.loop.mobile

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.loop.mobile.presentation.auth.login.LoginScreen
import com.loop.mobile.presentation.auth.login.LoginViewModel
import com.loop.mobile.presentation.search.SearchViewModel
import org.koin.compose.koinInject

@Composable
fun App() {
    MaterialTheme {
        val searchViewModel: SearchViewModel = koinInject()
        val loginViewModel: LoginViewModel = koinInject()
//        SearchScreen(viewModel = searchViewModel)
        LoginScreen(viewModel = loginViewModel)
    }
}
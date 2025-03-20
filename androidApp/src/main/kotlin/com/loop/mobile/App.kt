package com.loop.mobile

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.loop.mobile.presentation.search.SearchScreen
import com.loop.mobile.presentation.search.SearchViewModel
import org.koin.compose.koinInject

@Composable
fun App() {
    MaterialTheme {
        val searchViewModel: SearchViewModel = koinInject()
        SearchScreen(viewModel = searchViewModel)
    }
}
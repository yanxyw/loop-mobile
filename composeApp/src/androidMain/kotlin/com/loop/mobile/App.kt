package com.loop.mobile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import com.loop.mobile.presentation.search.SearchScreen
import com.loop.mobile.presentation.search.SearchViewModel

@Composable
fun App() {
    MaterialTheme {
        val viewModel = remember { SearchViewModel() }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchScreen(viewModel = viewModel)
        }
    }
}
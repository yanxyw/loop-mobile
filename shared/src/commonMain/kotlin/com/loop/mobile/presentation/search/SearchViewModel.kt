package com.loop.mobile.presentation.search

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class SearchViewModel {
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state

    fun onIntent(intent: SearchAction) {
        when (intent) {
            is SearchAction.OnQueryChange -> {
                _state.update { it.copy(query = intent.query) }
            }
            SearchAction.OnSearch -> {
                // Placeholder for search logic
                _state.update { it.copy(isLoading = true) }
            }
        }
    }
}
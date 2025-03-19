package com.loop.mobile.presentation.search

sealed interface SearchAction {
    data class OnQueryChange(val query: String) : SearchAction
    data object OnSearch : SearchAction
}
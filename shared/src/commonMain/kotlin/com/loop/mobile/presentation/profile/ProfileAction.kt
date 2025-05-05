package com.loop.mobile.presentation.profile

sealed interface ProfileAction {
    data object LoadProfile : ProfileAction
}
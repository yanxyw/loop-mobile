package com.loop.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.loop.mobile.data.local.TokenStorage
import com.loop.mobile.data.local.initTokenStorage
import com.loop.mobile.domain.auth.AuthStateManager
import com.loop.mobile.domain.auth.restoreAuthState
import com.loop.mobile.domain.repositories.UserRepository
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initTokenStorage(this)

        val tokenStorage: TokenStorage = getKoin().get()
        val userRepository: UserRepository = getKoin().get()
        val authStateManager: AuthStateManager = getKoin().get()

        lifecycleScope.launch {
            restoreAuthState(tokenStorage, userRepository, authStateManager)
            setContent {
                App()
            }
        }
    }
}
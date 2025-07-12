package com.loop.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.loop.mobile.data.local.SessionStorage
import com.loop.mobile.data.local.initSessionStorage
import com.loop.mobile.domain.auth.AuthStateManager
import com.loop.mobile.domain.auth.restoreAuthState
import com.loop.mobile.domain.repositories.UserRepository
import com.loop.mobile.presentation.SplashScreen
import com.loop.mobile.presentation.theme.AppTheme
import com.loop.mobile.presentation.theme.ThemeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.getKoin

class MainActivity : ComponentActivity() {
    private val themeManager: ThemeManager by lazy { getKoin().get() }
    private val sessionStorage: SessionStorage by lazy { getKoin().get() }
    private val userRepository: UserRepository by lazy { getKoin().get() }
    private val authStateManager: AuthStateManager by lazy { getKoin().get() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_Loop_App)
        initSessionStorage(this)

        setContent {
            AppTheme(themeManager) {
                SplashScreen()
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            restoreAuthState(sessionStorage, userRepository, authStateManager)
            withContext(Dispatchers.Main) {
                setContent {
                    AppTheme(themeManager) {
                        App(themeManager)
                    }
                }
            }
        }
    }
}
package com.loop.mobile

import android.app.Application
import com.loop.mobile.di.initKoin
import org.koin.android.ext.koin.androidContext

class LoopApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val baseUrl = BuildConfig.API_BASE_URL

        initKoin(
            appContext = { androidContext(this@LoopApp) },
            baseUrl = baseUrl
        )
    }
}
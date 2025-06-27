package com.loop.mobile

import android.app.Application
import com.loop.mobile.di.initKoin
import org.koin.android.ext.koin.androidContext

class LoopApp : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin(
            appContext = { androidContext(this@LoopApp) },
        )
    }
}
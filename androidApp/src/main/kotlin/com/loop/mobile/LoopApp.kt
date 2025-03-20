package com.loop.mobile

import android.app.Application
import com.loop.mobile.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class LoopApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@LoopApp)
            modules(appModule)
        }
    }
}
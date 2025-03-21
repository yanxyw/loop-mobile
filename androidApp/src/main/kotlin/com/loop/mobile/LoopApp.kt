package com.loop.mobile

import android.app.Application
import com.loop.mobile.di.initKoin

class LoopApp : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin()
    }
}
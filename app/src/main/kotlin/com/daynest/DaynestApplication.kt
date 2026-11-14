package com.daynest

import android.app.Application
import com.daynest.di.AppContainer

class DaynestApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

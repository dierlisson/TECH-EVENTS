package com.dierlisson.techevents

import android.app.Application
import com.dierlisson.techevents.core.di.AppContainer

class TechEventsApplication : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}

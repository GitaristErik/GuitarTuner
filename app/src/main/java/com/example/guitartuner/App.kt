package com.example.guitartuner

import android.app.Application
import com.example.guitartuner.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initDependencies()
    }

    private fun initDependencies() {
        startKoin {
//            if (!BuildConfig.RELEASE)
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
    }
}

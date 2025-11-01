package com.example.movieifoodtest

import android.app.Application
import com.example.movieifoodtest.core.network.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MoviesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MoviesApp)
            modules(
                networkModule
            )
        }
    }
}

package com.example.movieifoodtest

import android.app.Application
import com.example.movieifoodtest.di.databaseModule
import com.example.movieifoodtest.di.networkModule
import com.example.movieifoodtest.di.repositoryModule
import com.example.movieifoodtest.di.useCaseModule
import com.example.movieifoodtest.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MoviesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MoviesApp)
            modules(
                networkModule,
                databaseModule,
                repositoryModule,
                useCaseModule,
                viewModelModule
            )
        }
    }
}

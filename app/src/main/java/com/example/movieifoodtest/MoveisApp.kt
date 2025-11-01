package com.example.movieifoodtest

import android.app.Application
import com.example.movieifoodtest.core.di.databaseModule
import com.example.movieifoodtest.core.network.di.networkModule
import com.example.movieifoodtest.core.network.di.repositoryModule
import com.example.movieifoodtest.core.network.di.useCaseModule
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
//                viewModelModule
            )
        }
    }
}

package com.example.movieifoodtest

import android.app.Application
import com.example.movieifoodtest.data.database.di.databaseModule
import com.example.movieifoodtest.data.network.di.networkModule
import com.example.movieifoodtest.data.repository.di.repositoryModule
import com.example.movieifoodtest.domain.di.useCaseModule
import com.example.movieifoodtest.presentation.movies.di.viewModelModule
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

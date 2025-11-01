package com.example.movieifoodtest.di

import com.example.movieifoodtest.BuildConfig
import com.example.movieifoodtest.data.network.http.createJson
import com.example.movieifoodtest.data.network.http.provideAuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module

val networkModule = module {
    single { createJson() }

    single {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(
                provideAuthInterceptor(
                    bearer = BuildConfig.TMDB_BEARER,
                    apiKey = BuildConfig.TMDB_API_KEY
                )
            )
            .addInterceptor(logging)
            .build()
    }
}

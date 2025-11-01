package com.example.movieifoodtest.di

import com.example.movieifoodtest.BuildConfig
import com.example.movieifoodtest.data.network.http.createJson
import com.example.movieifoodtest.data.network.http.provideAuthInterceptor
import com.example.movieifoodtest.data.network.tmdb.TmdbApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

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

    single {
        val json: Json = get()
        val contentType = "application/json".toMediaType()

        Retrofit.Builder()
            .baseUrl(BuildConfig.TMDB_BASE_URL)
            .client(get())
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    single<TmdbApi> { get<Retrofit>().create(TmdbApi::class.java) }
}

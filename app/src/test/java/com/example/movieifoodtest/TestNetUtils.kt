package com.example.movieifoodtest


import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

fun testRetrofit(baseUrl: String, json: Json = Json { ignoreUnknownKeys = true }): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(OkHttpClient())
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
}

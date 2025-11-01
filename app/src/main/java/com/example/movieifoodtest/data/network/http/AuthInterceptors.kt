package com.example.movieifoodtest.data.network.http

import okhttp3.HttpUrl
import okhttp3.Interceptor

fun provideAuthInterceptor(
    bearer: String?,
    apiKey: String?
): Interceptor = Interceptor { chain ->
    val original = chain.request()

    val requestBuilder = original.newBuilder()
        .addHeader("Accept", "application/json")
        .apply {
            if (!bearer.isNullOrBlank()) {
                addHeader("Authorization", "Bearer $bearer")
            }
        }

    val url: HttpUrl = original.url.newBuilder().apply {
        if (!apiKey.isNullOrBlank()) addQueryParameter("api_key", apiKey)
    }.build()

    val request = requestBuilder.url(url).build()
    chain.proceed(request)
}

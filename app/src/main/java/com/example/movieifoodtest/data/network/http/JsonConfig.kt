package com.example.movieifoodtest.data.network.http

import kotlinx.serialization.json.Json

fun createJson(): Json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    explicitNulls = false
    isLenient = true
}
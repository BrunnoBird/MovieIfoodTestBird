package com.example.movieifoodtest.core.network.http

import kotlinx.serialization.json.Json

fun createJson(): Json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    explicitNulls = false
    isLenient = true
}
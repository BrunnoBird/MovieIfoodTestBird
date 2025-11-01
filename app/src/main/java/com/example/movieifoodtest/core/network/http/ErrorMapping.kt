package com.example.movieifoodtest.core.network.http

import retrofit2.HttpException
import java.io.IOException

sealed interface NetworkError {
    data class Http(val code: Int, val message: String?) : NetworkError
    data class Io(val cause: IOException): NetworkError
    data class Unknown(val cause: Throwable): NetworkError
}

fun Throwable.toNetworkError(): NetworkError = when (this) {
    is HttpException -> NetworkError.Http(code(), message())
    is IOException -> NetworkError.Io(this)
    else -> NetworkError.Unknown(this)
}

package com.example.movieifoodtest.data.mapper

import com.example.movieifoodtest.data.network.http.NetworkError
import com.example.movieifoodtest.data.network.http.toNetworkError
import com.example.movieifoodtest.domain.model.DomainError
import com.example.movieifoodtest.domain.model.DomainException

fun Throwable.toDomainException(): DomainException {
    return when (val ne = this.toNetworkError()) {
        is NetworkError.Http -> when (ne.code) {
            401 -> DomainException(DomainError.Unauthorized)
            404 -> DomainException(DomainError.NotFound)
            else -> DomainException(DomainError.Http(ne.code, ne.message))
        }
        is NetworkError.Io -> DomainException(DomainError.Network)
        is NetworkError.Unknown -> DomainException(DomainError.Unknown(ne.cause.message))
    }
}

inline fun <T> resultCatching(block: () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (t: Throwable) {
        Result.failure(t.toDomainException())
    }

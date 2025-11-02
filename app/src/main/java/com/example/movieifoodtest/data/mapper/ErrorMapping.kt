package com.example.movieifoodtest.data.mapper

import com.example.movieifoodtest.data.network.http.NetworkError
import com.example.movieifoodtest.data.network.http.toNetworkError
import com.example.movieifoodtest.domain.model.DomainError
import com.example.movieifoodtest.domain.model.DomainException
import com.example.movieifoodtest.domain.model.DomainResult
import kotlinx.coroutines.CancellationException

fun Throwable.toDomainException(): DomainException {
    if (this is DomainException) return this
    return DomainException(this.toNetworkError().toDomainError())
}

internal fun NetworkError.toDomainError(): DomainError = when (this) {
    is NetworkError.Http -> when (code) {
        401 -> DomainError.Unauthorized
        404 -> DomainError.NotFound
        else -> DomainError.Http(code, message)
    }

    is NetworkError.Io -> DomainError.Network
    is NetworkError.Unknown -> DomainError.Unknown(
        cause.message ?: cause.toString()
    )
}

inline fun <T> domainResultCatching(block: () -> T): DomainResult<T> =
    try {
        DomainResult.success(block())
    } catch (t: Throwable) {
        if (t is CancellationException) throw t
        DomainResult.failure(t.toDomainException())
    }

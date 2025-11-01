package com.example.movieifoodtest.domain.model

sealed class DomainResult<out T> {
    data class Success<out T>(val value: T) : DomainResult<T>()
    data class Failure(val exception: DomainException) : DomainResult<Nothing>()

    val isSuccess: Boolean get() = this is Success<*>
    val isFailure: Boolean get() = this is Failure

    companion object {
        fun <T> success(value: T): DomainResult<T> = Success(value)
        fun failure(exception: DomainException): DomainResult<Nothing> = Failure(exception)
        fun failure(error: DomainError): DomainResult<Nothing> = Failure(DomainException(error))
    }
}

inline fun <T> DomainResult<T>.onSuccess(action: (T) -> Unit): DomainResult<T> {
    if (this is DomainResult.Success) action(value)
    return this
}

inline fun <T> DomainResult<T>.onFailure(action: (DomainException) -> Unit): DomainResult<T> {
    if (this is DomainResult.Failure) action(exception)
    return this
}

fun <T> DomainResult<T>.getOrNull(): T? = when (this) {
    is DomainResult.Success -> value
    is DomainResult.Failure -> null
}

fun <T> DomainResult<T>.exceptionOrNull(): DomainException? = when (this) {
    is DomainResult.Success -> null
    is DomainResult.Failure -> exception
}

fun <T> DomainResult<T>.getOrThrow(): T = when (this) {
    is DomainResult.Success -> value
    is DomainResult.Failure -> throw exception
}

inline fun <T, R> DomainResult<T>.map(transform: (T) -> R): DomainResult<R> = when (this) {
    is DomainResult.Success -> DomainResult.Success(transform(value))
    is DomainResult.Failure -> DomainResult.Failure(exception)
}

inline fun <T, R> DomainResult<T>.fold(
    onSuccess: (T) -> R,
    onFailure: (DomainException) -> R,
): R = when (this) {
    is DomainResult.Success -> onSuccess(value)
    is DomainResult.Failure -> onFailure(exception)
}

package com.example.movieifoodtest.domain.model

sealed interface DomainError {
    data object Unauthorized : DomainError
    data object NotFound : DomainError
    data class Http(val code: Int, val message: String?) : DomainError
    data object Network : DomainError
    data class Unknown(val message: String?) : DomainError
}

class DomainException(val domain: DomainError) : RuntimeException(
    when (domain) {
        is DomainError.Http -> "Http ${domain.code} - ${domain.message}"
        is DomainError.Unknown -> "Unknown - ${domain.message}"
        DomainError.Network -> "Network"
        DomainError.NotFound -> "NotFound"
        DomainError.Unauthorized -> "Unauthorized"
    }
)

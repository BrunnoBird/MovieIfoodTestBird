package com.example.movieifoodtest.core.network.tmdb.dto

import kotlinx.serialization.Serializable

@Serializable
data class PagedResponse<T>(
    val page: Int,
    val results: List<T>
)
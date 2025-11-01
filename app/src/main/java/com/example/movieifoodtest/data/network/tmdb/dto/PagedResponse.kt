package com.example.movieifoodtest.data.network.tmdb.dto

import kotlinx.serialization.Serializable

@Serializable
data class PagedResponse<T>(
    val page: Int,
    val results: List<T>
)
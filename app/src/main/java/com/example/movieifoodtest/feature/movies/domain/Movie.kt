package com.example.movieifoodtest.feature.movies.domain

data class Movie(
    val id: Long,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val rating: Double
)

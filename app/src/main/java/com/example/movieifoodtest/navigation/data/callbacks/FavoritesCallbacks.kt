package com.example.movieifoodtest.navigation.data.callbacks

import com.example.movieifoodtest.domain.model.Movie

data class FavoritesCallbacks(
    val onMovieSelected: (Movie) -> Unit,
    val onBackClick: () -> Unit,
)
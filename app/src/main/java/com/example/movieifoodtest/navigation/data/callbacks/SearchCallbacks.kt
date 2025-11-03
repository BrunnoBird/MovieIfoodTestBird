package com.example.movieifoodtest.navigation.data.callbacks

import com.example.movieifoodtest.domain.model.Movie

data class SearchCallbacks(
    val onMovieSelected: (Movie) -> Unit,
    val onFavoriteClick: () -> Unit,
)
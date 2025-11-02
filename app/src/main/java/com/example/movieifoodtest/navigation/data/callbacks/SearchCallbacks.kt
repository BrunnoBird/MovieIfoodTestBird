package com.example.movieifoodtest.navigation.data.callbacks

import com.example.movieifoodtest.domain.model.Movie

internal data class SearchCallbacks(
    val onMovieSelected: (Movie) -> Unit,
    val onFavoriteClick: () -> Unit,
)
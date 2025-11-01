package com.example.movieifoodtest.presentation.movies.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieifoodtest.domain.model.Movie
import com.example.movieifoodtest.domain.usecase.ObserveFavoritesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class FavoritesUiState(
    val items: List<Movie> = emptyList()
)

class FavoritesViewModel(
    observeFavorites: ObserveFavoritesUseCase
) : ViewModel() {
    val uiState = observeFavorites()
        .map { FavoritesUiState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FavoritesUiState())
}

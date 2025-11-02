package com.example.movieifoodtest.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieifoodtest.domain.model.Movie
import com.example.movieifoodtest.domain.model.fold
import com.example.movieifoodtest.domain.usecase.GetMovieDetailsUseCase
import com.example.movieifoodtest.domain.usecase.ObserveFavoritesUseCase
import com.example.movieifoodtest.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MovieDetailsUiState(
    val loading: Boolean = true,
    val data: Movie? = null,
    val error: String? = null,
    val isFavorite: Boolean = false,
    val toggleError: String? = null,
    val favoriteChanged: Boolean? = null
)

class MovieDetailsViewModel(
    private val id: Long,
    private val details: GetMovieDetailsUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    observeFavorites: ObserveFavoritesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailsUiState())
    val uiState: StateFlow<MovieDetailsUiState> = _uiState

    init {
        load()
        observeFavorites(observeFavorites)
    }

    private fun load() = viewModelScope.launch {
        _uiState.value = MovieDetailsUiState(loading = true)

        val result = details(id)

        _uiState.update { currentState ->
            result.fold(
                onSuccess = { movie ->
                    MovieDetailsUiState(
                        loading = false,
                        data = movie,
                        isFavorite = currentState.isFavorite
                    )
                },
                onFailure = { error ->
                    MovieDetailsUiState(
                        loading = false,
                        error = error.message ?: "Unknown error"
                    )
                }
            )
        }
    }

    fun retry() = load()

    fun onToggleFavorite() = viewModelScope.launch {
        _uiState.value.data?.let { movie ->
            val result = toggleFavorite(movie)

            _uiState.update { currentState ->
                result.fold(
                    onSuccess = { favorite ->
                        currentState.copy(
                            isFavorite = favorite,
                            toggleError = null,
                            favoriteChanged = favorite
                        )
                    },
                    onFailure = { error ->
                        currentState.copy(
                            toggleError = error.message ?: "Unknown error",
                            favoriteChanged = null
                        )
                    }
                )
            }
        }
    }

    fun onFavoriteChangeConsumed() {
        _uiState.update { it.copy(favoriteChanged = null) }
    }

    fun onToggleErrorConsumed() {
        _uiState.update { it.copy(toggleError = null) }
    }

    private fun observeFavorites(observeFavorites: ObserveFavoritesUseCase) =
        viewModelScope.launch {
            observeFavorites().collectLatest { favorites ->
                val isFavorite = favorites.any { it.id == id }
                _uiState.update { it.copy(isFavorite = isFavorite) }
            }
        }
}

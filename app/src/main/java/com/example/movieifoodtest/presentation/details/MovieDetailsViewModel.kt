package com.example.movieifoodtest.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieifoodtest.domain.model.Movie
import com.example.movieifoodtest.domain.model.fold
import com.example.movieifoodtest.domain.usecase.GetMovieDetailsUseCase
import com.example.movieifoodtest.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val toggleFavorite: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailsUiState())
    val uiState: StateFlow<MovieDetailsUiState> = _uiState

    init {
        load()
    }

    private fun load() = viewModelScope.launch {
        _uiState.value = MovieDetailsUiState(loading = true)

        val result = details(id)

        _uiState.update {
            result.fold(
                onSuccess = { movie ->
                    MovieDetailsUiState(
                        loading = false,
                        data = movie
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
                    onSuccess = {
                        currentState.copy(
                            isFavorite = currentState.isFavorite,
                            toggleError = null,
                            favoriteChanged = currentState.favoriteChanged
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
}

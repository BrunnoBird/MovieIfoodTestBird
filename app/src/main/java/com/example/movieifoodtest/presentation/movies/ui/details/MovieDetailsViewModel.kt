package com.example.movieifoodtest.presentation.movies.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieifoodtest.domain.model.DomainResult
import com.example.movieifoodtest.domain.model.Movie
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

        when (val result = details(id)) {
            is DomainResult.Success -> {
                _uiState.update {
                    MovieDetailsUiState(
                        loading = false,
                        data = result.value
                    )
                }
            }

            is DomainResult.Failure -> {
                _uiState.update {
                    MovieDetailsUiState(
                        loading = false,
                        error = result.exception.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun retry() {
        load()
    }

    fun onToggleFavorite() = viewModelScope.launch {
        _uiState.value.data?.let { movie ->
            when (val result = toggleFavorite(movie)) {
                is DomainResult.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isFavorite = currentState.isFavorite,
                            toggleError = null,
                            favoriteChanged = currentState.favoriteChanged
                        )
                    }
                }

                is DomainResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            toggleError = result.exception.message ?: "Unknown error",
                            favoriteChanged = null
                        )
                    }
                }
            }
        }
    }
}

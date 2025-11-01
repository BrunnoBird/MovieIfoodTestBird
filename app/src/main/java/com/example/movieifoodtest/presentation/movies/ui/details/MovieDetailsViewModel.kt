package com.example.movieifoodtest.presentation.movies.ui.details

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieifoodtest.domain.model.DomainResult
import com.example.movieifoodtest.domain.model.Movie
import com.example.movieifoodtest.domain.usecase.GetMovieDetailsUseCase
import com.example.movieifoodtest.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.launch

data class MovieDetailsUiState(
    val loading: Boolean = true,
    val data: Movie? = null,
    val error: String? = null
)

class MovieDetailsViewModel(
    private val id: Long,
    private val details: GetMovieDetailsUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase
) : ViewModel() {

    val state = mutableStateOf(MovieDetailsUiState())

    init {
        load()
    }

    private fun load() = viewModelScope.launch {
        state.value = MovieDetailsUiState(loading = true)

        when (val result = details(id)) {
            is DomainResult.Success -> {
                state.value = MovieDetailsUiState(loading = false, data = result.value)
            }

            is DomainResult.Failure -> {
                state.value = MovieDetailsUiState(
                    loading = false,
                    error = result.exception.message ?: "Unknown error"
                )
            }
        }
    }

    fun onToggleFavorite() = viewModelScope.launch {
        state.value.data?.let { movie ->
            when (toggleFavorite(movie)) {
                is DomainResult.Success -> Unit // Toast de feedback tambem adicionado futuramente
                is DomainResult.Failure -> Unit // Toast de Feedback feedback pode ser adicionado futuramente
            }
        }
    }
}

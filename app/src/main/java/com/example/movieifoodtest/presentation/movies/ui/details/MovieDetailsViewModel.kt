package com.example.movieifoodtest.presentation.movies.ui.details

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    init { load() }

    private fun load() = viewModelScope.launch {
        state.value = MovieDetailsUiState(loading = true)
        details(id)
            .onSuccess { state.value = MovieDetailsUiState(loading = false, data = it) }
            .onFailure { state.value = MovieDetailsUiState(loading = false, error = it.message ?: "Unknown error") }
    }

    fun onToggleFavorite() = viewModelScope.launch {
        state.value.data?.let { movie ->
            toggleFavorite(movie)
            // regra simples: sem feedback; se quiser confirmar sucesso/erro, dá pra atualizar estado aqui
        }
    }
}

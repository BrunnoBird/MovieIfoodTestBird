package com.example.movieifoodtest.presentation.movies.ui.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieifoodtest.domain.model.Movie
import com.example.movieifoodtest.domain.usecase.SearchMoviesUseCase
import kotlinx.coroutines.launch

data class MoviesListUiState(
    val query: String = "",
    val loading: Boolean = false,
    val items: List<Movie> = emptyList(),
    val error: String? = null
)

class MoviesListViewModel(
    private val search: SearchMoviesUseCase
) : ViewModel() {

    var uiState by mutableStateOf(MoviesListUiState())
        private set

    fun onQueryChange(q: String) {
        uiState = uiState.copy(query = q)
    }

    fun search() = viewModelScope.launch {
        val q = uiState.query.trim()
        if (q.isEmpty()) {
            uiState = uiState.copy(items = emptyList(), error = null, loading = false)
            return@launch
        }
        uiState = uiState.copy(loading = true, error = null)
        search(q, 1)
            .onSuccess { uiState = uiState.copy(loading = false, items = it) }
            .onFailure { uiState = uiState.copy(loading = false, error = it.message ?: "Unknown error") }
    }
}

package com.example.movieifoodtest.presentation.movies.ui.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieifoodtest.domain.model.DomainResult
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
        val currentQuery = uiState.query.trim()
        uiState = uiState.copy(loading = true, error = null)

        uiState = when (val result = search(currentQuery, 1)) {
            is DomainResult.Success -> {
                uiState.copy(loading = false, items = result.value)
            }

            is DomainResult.Failure -> {
                uiState.copy(
                    loading = false,
                    error = result.exception.message ?: "Unknown error"
                )
            }
        }
    }
}

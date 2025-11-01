package com.example.movieifoodtest.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieifoodtest.domain.model.DomainResult
import com.example.movieifoodtest.domain.model.Movie
import com.example.movieifoodtest.domain.usecase.SearchMoviesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
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

    private val _uiState = MutableStateFlow(MoviesListUiState())
    val uiState: StateFlow<MoviesListUiState> = _uiState

    fun onQueryChange(q: String) {
        _uiState.update { it.copy(query = q) }
    }

    fun search() = viewModelScope.launch {
        val currentQuery = _uiState.value.query.trim()
        _uiState.update { it.copy(loading = true, error = null) }

        val result = search(currentQuery, 1)

        _uiState.update { currentState ->
            when (result) {
                is DomainResult.Success -> {
                    currentState.copy(loading = false, items = result.value)
                }

                is DomainResult.Failure -> {
                    currentState.copy(
                        loading = false,
                        error = result.exception.message ?: "Unknown error"
                    )
                }
            }
        }
    }
}

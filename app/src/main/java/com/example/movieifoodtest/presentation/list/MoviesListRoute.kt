package com.example.movieifoodtest.presentation.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.movieifoodtest.domain.model.Movie
import org.koin.androidx.compose.koinViewModel

@Composable
fun MoviesListRoute(
    modifier: Modifier = Modifier,
    onMovieSelected: (Movie) -> Unit,
    onFavoriteClick: () -> Unit,
    viewModel: MoviesListViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    MoviesListScreen(
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onSearch = viewModel::search,
        onMovieSelected = onMovieSelected,
        onFavoriteClick = onFavoriteClick,
        modifier = modifier
    )
}
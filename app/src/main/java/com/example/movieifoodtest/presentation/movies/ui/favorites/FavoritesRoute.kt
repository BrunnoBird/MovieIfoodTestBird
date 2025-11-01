package com.example.movieifoodtest.presentation.movies.ui.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.movieifoodtest.domain.model.Movie
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritesRoute(
    onMovieSelected: (Movie) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    FavoritesScreen(
        state = state,
        onMovieSelected = onMovieSelected,
        modifier = modifier
    )
}
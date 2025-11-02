package com.example.movieifoodtest.presentation.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.movieifoodtest.domain.model.Movie
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritesRoute(
    modifier: Modifier = Modifier,
    onMovieSelected: (Movie) -> Unit,
    onBackClick: () -> Unit,
    viewModel: FavoritesViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    FavoritesScreen(
        state = state,
        onMovieSelected = onMovieSelected,
        onBackClick = onBackClick,
        modifier = modifier
    )
}
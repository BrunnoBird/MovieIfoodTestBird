package com.example.movieifoodtest.presentation.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsRoute(
    movieId: Long,
    onDismiss: () -> Unit,
    onToggleSuccess: (Boolean) -> Unit,
    onToggleFailure: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MovieDetailsViewModel = koinViewModel(parameters = { parametersOf(movieId) })
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(state.toggleError) {
        state.toggleError?.let { onToggleFailure(it) }
    }
    LaunchedEffect(state.favoriteChanged) {
        state.favoriteChanged?.let { onToggleSuccess(it) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = sheetState
    ) {
        when {
            state.loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> state.error?.let {
                ErrorContent(
                    message = it,
                    onRetry = viewModel::retry,
                    onClose = onDismiss
                )
            }

            state.data != null -> state.data?.let {
                MovieDetailsContent(
                    movie = it,
                    isFavorite = state.isFavorite,
                    onToggleFavorite = viewModel::onToggleFavorite,
                    modifier = Modifier.padding(24.dp)
                )
            }
        }
    }
}

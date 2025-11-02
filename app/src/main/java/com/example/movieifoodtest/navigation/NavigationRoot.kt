package com.example.movieifoodtest.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.movieifoodtest.navigation.data.callbacks.DetailCallbacks
import com.example.movieifoodtest.navigation.data.callbacks.FavoritesCallbacks
import com.example.movieifoodtest.navigation.data.callbacks.SearchCallbacks
import com.example.movieifoodtest.navigation.data.screens.DetailScreen
import com.example.movieifoodtest.navigation.data.screens.FavoritesScreen
import com.example.movieifoodtest.navigation.data.screens.SearchScreen
import com.example.movieifoodtest.presentation.details.MovieDetailsRoute
import com.example.movieifoodtest.presentation.favorites.FavoritesRoute
import com.example.movieifoodtest.presentation.list.MoviesListRoute

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier,
    onToggleSuccess: () -> Unit,
    onToggleFailure: (message: String) -> Unit,
) {
    val backStack = rememberNavBackStack(SearchScreen())

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryDecorators = listOf(
        ),
        entryProvider = { key ->
            when (key) {
                is SearchScreen -> {
                    NavEntry(
                        key = key,
                    ) {
                        val callbacks = createSearchCallbacks(
                            onNavigateToDetail = { movieId ->
                                backStack.add(
                                    DetailScreen(
                                        id = movieId
                                    )
                                )
                            },
                            onNavigateToFavorites = {
                                backStack.add(FavoritesScreen)
                            }
                        )
                        MoviesListRoute(
                            onMovieSelected = callbacks.onMovieSelected,
                            onFavoriteClick = callbacks.onFavoriteClick,
                        )
                    }
                }

                is FavoritesScreen -> {
                    NavEntry(
                        key = key,
                    ) { entry ->
                        val callbacks = createFavoritesCallbacks(
                            onNavigateToDetail = { movieId ->
                                backStack.add(
                                    DetailScreen(
                                        id = movieId
                                    )
                                )
                            },
                            onBack = {
                                backStack.remove(entry)
                            }
                        )
                        FavoritesRoute(
                            onMovieSelected = callbacks.onMovieSelected,
                            onBackClick = callbacks.onBackClick,
                        )
                    }
                }

                is DetailScreen -> {
                    NavEntry(
                        key = key,
                    ) { entry ->
                        val callbacks = createDetailCallbacks(
                            onClose = {
                                backStack.remove(entry)
                            },
                            onToggleSucceeded = onToggleSuccess,
                            onToggleFailed = onToggleFailure,
                        )
                        MovieDetailsRoute(
                            movieId = key.id,
                            onDismiss = callbacks.onDismiss,
                            onToggleSuccess = callbacks.onToggleSuccess,
                            onToggleFailure = callbacks.onToggleFailure,
                        )
                    }
                }

                else -> throw RuntimeException("Invalid NavKey.")
            }
        },
    )
}

internal fun createSearchCallbacks(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToFavorites: () -> Unit,
): SearchCallbacks {
    return SearchCallbacks(
        onMovieSelected = { movie ->
            onNavigateToDetail(movie.id)
        },
        onFavoriteClick = {
            onNavigateToFavorites()
        }
    )
}

internal fun createFavoritesCallbacks(
    onNavigateToDetail: (Long) -> Unit,
    onBack: () -> Unit,
): FavoritesCallbacks {
    return FavoritesCallbacks(
        onMovieSelected = { movie ->
            onNavigateToDetail(movie.id)
        },
        onBackClick = {
            onBack()
        }
    )
}

internal fun createDetailCallbacks(
    onClose: () -> Unit,
    onToggleSucceeded: () -> Unit,
    onToggleFailed: (String) -> Unit,
): DetailCallbacks {
    return DetailCallbacks(
        onDismiss = {
            onClose()
        },
        onToggleSuccess = {
            onClose()
            onToggleSucceeded()
        },
        onToggleFailure = { message ->
            onClose()
            onToggleFailed(message)
        }
    )
}
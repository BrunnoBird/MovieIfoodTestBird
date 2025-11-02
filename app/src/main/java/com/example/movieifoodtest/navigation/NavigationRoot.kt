package com.example.movieifoodtest.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.movieifoodtest.domain.model.Movie
import com.example.movieifoodtest.presentation.details.MovieDetailsRoute
import com.example.movieifoodtest.presentation.favorites.FavoritesRoute
import com.example.movieifoodtest.presentation.list.MoviesListRoute
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class SearchScreen(val movieId: Long? = null) : NavKey

@Serializable
data object FavoritesScreen : NavKey

@Serializable
data class DetailScreen(
    val id: Long,
    val onDismiss: () -> Unit = {},
    val onToggleSuccess: () -> Unit = {},
    val onToggleFailure: () -> Unit = {}
) : NavKey

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
                        MoviesListRoute(
                            viewModel = koinViewModel {
                                parametersOf(key.movieId)
                            },
                            onMovieSelected = { movie: Movie ->
                                backStack.add(
                                    DetailScreen(
                                        id = movie.id
                                    )
                                )
                            },
                            onFavoriteClick = {
                                backStack.add(FavoritesScreen)
                            }
                        )
                    }
                }

                is FavoritesScreen -> {
                    NavEntry(
                        key = key,
                    ) {
                        FavoritesRoute(
                            onMovieSelected = { movie: Movie ->
                                backStack.add(
                                    DetailScreen(
                                        id = movie.id
                                    )
                                )
                            }
                        )
                    }
                }

                is DetailScreen -> {
                    NavEntry(
                        key = key,
                    ) {
                        MovieDetailsRoute(
                            viewModel = koinViewModel(
                                key = "movie_details_${key.id}"
                            ) {
                                parametersOf(key.id)
                            },
                            movieId = key.id,
                            onDismiss = {
                                backStack.remove(it)
                            },
                            onToggleSuccess = { isSuccess ->
                                backStack.remove(it)
                                onToggleSuccess()
                            },
                            onToggleFailure = { message ->
                                backStack.remove(it)
                                onToggleFailure(message)
                            }
                        )
                    }
                }

                else -> throw RuntimeException("Invalid NavKey.")
            }
        },
    )
}
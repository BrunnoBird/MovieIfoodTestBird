package com.example.movieifoodtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.movieifoodtest.domain.model.Movie
import com.example.movieifoodtest.presentation.movies.ui.details.MovieDetailsRoute
import com.example.movieifoodtest.presentation.movies.ui.favorites.FavoritesRoute
import com.example.movieifoodtest.presentation.movies.ui.list.MoviesListRoute
import com.example.movieifoodtest.ui.theme.MovieIfoodTestTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieIfoodTestTheme {
                MovieIfoodTestApp()
            }
        }
    }
}

@Composable
fun MovieIfoodTestApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestination.HOME) }
    var selectedMovieId by rememberSaveable { mutableStateOf<Long?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            NavigationBar {
                AppDestination.entries.forEach { destination ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.label
                            )
                        },
                        label = { Text(destination.label) },
                        selected = destination == currentDestination,
                        onClick = { currentDestination = destination }
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        when (currentDestination) {
            AppDestination.HOME -> MoviesListRoute(
                onMovieSelected = { movie: Movie -> selectedMovieId = movie.id },
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )

            AppDestination.FAVORITES -> FavoritesRoute(
                onMovieSelected = { movie: Movie -> selectedMovieId = movie.id },
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )
        }
    }

    selectedMovieId?.let { movieId ->
        MovieDetailsRoute(
            movieId = movieId,
            onDismiss = { selectedMovieId = null },
            onToggleSuccess = { favorited ->
                coroutineScope.launch {
                    val message = if (favorited) {
                        "Filme adicionado aos favoritos"
                    } else {
                        "Filme removido dos favoritos"
                    }
                    snackbarHostState.showSnackbar(message)
                }
            },
            onToggleFailure = { message ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(message)
                }
            }
        )
    }

    selectedMovieId?.let { movieId ->
        MovieDetailsRoute(
            movieId = movieId,
            onDismiss = { selectedMovieId = null },
            onToggleSuccess = { favorited ->
                coroutineScope.launch {
                    val message = if (favorited) {
                        "Filme adicionado aos favoritos"
                    } else {
                        "Filme removido dos favoritos"
                    }
                    snackbarHostState.showSnackbar(message)
                }
            },
            onToggleFailure = { message ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(message)
                }
            }
        )
    }
}

enum class AppDestination(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Buscar", Icons.Default.Home),
    FAVORITES("Favoritos", Icons.Default.Favorite),
}

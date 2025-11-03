package com.example.movieifoodtest.presentation.list

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.movieifoodtest.domain.model.Movie
import org.junit.Rule
import org.junit.Test

class MoviesListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenMovies_whenScreenIsDisplayed_shouldShowMovieList() {
        val movies = listOf(
            Movie(id = 1, title = "Movie 1", overview = "Overview 1", posterUrl = null, rating = 8.0),
            Movie(id = 2, title = "Movie 2", overview = "Overview 2", posterUrl = null, rating = 8.5)
        )
        val uiState = MoviesListUiState(items = movies)

        composeTestRule.setContent {
            MoviesListScreen(
                state = uiState,
                onQueryChange = {},
                onSearch = {},
                onMovieSelected = {},
                onFavoriteClick = {}
            )
        }

        composeTestRule.onNodeWithText("Movie 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Movie 2").assertIsDisplayed()
    }
}
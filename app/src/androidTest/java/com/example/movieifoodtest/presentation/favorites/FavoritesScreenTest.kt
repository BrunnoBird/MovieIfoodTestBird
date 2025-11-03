package com.example.movieifoodtest.presentation.favorites

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.movieifoodtest.domain.model.Movie
import org.junit.Rule
import org.junit.Test

class FavoritesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenFavoriteMovies_whenScreenIsDisplayed_shouldShowMovieList() {
        val movies = listOf(
            Movie(id = 1, title = "Favorite Movie 1", overview = "Overview 1", posterUrl = null, rating = 8.0),
            Movie(id = 2, title = "Favorite Movie 2", overview = "Overview 2", posterUrl = null, rating = 8.5)
        )
        val uiState = FavoritesUiState(items = movies)

        composeTestRule.setContent {
            FavoritesScreen(
                state = uiState,
                onMovieSelected = {},
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithText("Favorite Movie 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Favorite Movie 2").assertIsDisplayed()
    }
}
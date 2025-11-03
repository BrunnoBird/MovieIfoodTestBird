package com.example.movieifoodtest.presentation.details

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.movieifoodtest.domain.model.Movie
import org.junit.Rule
import org.junit.Test

class MovieDetailsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenMovie_whenScreenIsDisplayed_shouldShowMovieDetails() {
        val movie = Movie(
            id = 1,
            title = "Test Movie Title",
            overview = "This is a test movie overview.",
            posterUrl = null,
            rating = 8.5
        )

        composeTestRule.setContent {
            MovieDetailsContent(
                movie = movie,
                isFavorite = false,
                onToggleFavorite = {}
            )
        }

        composeTestRule.onNodeWithText("Test Movie Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("This is a test movie overview.").assertIsDisplayed()
    }
}
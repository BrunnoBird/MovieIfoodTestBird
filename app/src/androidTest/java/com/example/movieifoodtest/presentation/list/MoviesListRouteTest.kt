
package com.example.movieifoodtest.presentation.list

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.movieifoodtest.domain.model.Movie
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MoviesListRouteTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val onMovieSelected: (Movie) -> Unit = mockk(relaxed = true)
    private val onFavoriteClick: () -> Unit = mockk(relaxed = true)
    private lateinit var viewModel: MoviesListViewModel

    @Before
    fun setup() {
        viewModel = mockk(relaxed = true)
    }

    @Test
    fun givenErrorState_whenRouteIsDisplayed_shouldShowErrorMessage() {
        // Given
        val errorMessage = "An unexpected error occurred"
        val uiState = MutableStateFlow(MoviesListUiState(error = errorMessage))
        every { viewModel.uiState } returns uiState

        // When
        composeTestRule.setContent {
            MoviesListRoute(
                onMovieSelected = onMovieSelected,
                onFavoriteClick = onFavoriteClick,
                viewModel = viewModel
            )
        }

        // Then
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithText(errorMessage).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun givenSuccessState_whenRouteIsDisplayed_shouldShowMoviesList() {
        // Given
        val movies = listOf(Movie(1, "Movie 1", "Overview 1", null, 8.0))
        val uiState = MutableStateFlow(MoviesListUiState(items = movies))
        every { viewModel.uiState } returns uiState

        // When
        composeTestRule.setContent {
            MoviesListRoute(
                onMovieSelected = onMovieSelected,
                onFavoriteClick = onFavoriteClick,
                viewModel = viewModel
            )
        }

        // Then
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithText("Movie 1").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Movie 1").assertIsDisplayed()
    }

    @Test
    fun whenMovieIsClicked_shouldCallOnMovieSelected() {
        // Given
        val movie = Movie(1, "Movie 1", "Overview 1", null, 8.0)
        val uiState = MutableStateFlow(MoviesListUiState(items = listOf(movie)))
        every { viewModel.uiState } returns uiState

        // When
        composeTestRule.setContent {
            MoviesListRoute(
                onMovieSelected = onMovieSelected,
                onFavoriteClick = onFavoriteClick,
                viewModel = viewModel
            )
        }
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithText("Movie 1").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Movie 1").performClick()

        // Then
        verify { onMovieSelected(movie) }
    }
}

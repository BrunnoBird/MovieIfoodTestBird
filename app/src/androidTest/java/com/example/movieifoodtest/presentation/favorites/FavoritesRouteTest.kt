
package com.example.movieifoodtest.presentation.favorites

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.movieifoodtest.R
import com.example.movieifoodtest.domain.model.Movie
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavoritesRouteTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val onMovieSelected: (Movie) -> Unit = mockk(relaxed = true)
    private val onBackClick: () -> Unit = mockk(relaxed = true)
    private lateinit var viewModel: FavoritesViewModel

    @Before
    fun setup() {
        viewModel = mockk(relaxed = true)
    }

    @Test
    fun givenEmptyState_whenRouteIsDisplayed_shouldShowEmptyMessage() {
        // Given
        val uiState = MutableStateFlow(FavoritesUiState(items = emptyList()))
        every { viewModel.uiState } returns uiState
        lateinit var emptyStateText: String

        // When
        composeTestRule.setContent {
            emptyStateText = composeTestRule.activity.getString(R.string.favorite_screen_text_empty_state)
            FavoritesRoute(
                onMovieSelected = onMovieSelected,
                onBackClick = onBackClick,
                viewModel = viewModel
            )
        }

        // Then
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithText(emptyStateText).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(emptyStateText).assertIsDisplayed()
    }

    @Test
    fun givenSuccessState_whenRouteIsDisplayed_shouldShowFavoritesList() {
        // Given
        val movies = listOf(Movie(1, "Favorite Movie 1", "Overview 1", null, 8.0))
        val uiState = MutableStateFlow(FavoritesUiState(items = movies))
        every { viewModel.uiState } returns uiState

        // When
        composeTestRule.setContent {
            FavoritesRoute(
                onMovieSelected = onMovieSelected,
                onBackClick = onBackClick,
                viewModel = viewModel
            )
        }

        // Then
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithText("Favorite Movie 1").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Favorite Movie 1").assertIsDisplayed()
    }

    @Test
    fun whenMovieIsClicked_shouldCallOnMovieSelected() {
        // Given
        val movie = Movie(1, "Favorite Movie 1", "Overview 1", null, 8.0)
        val uiState = MutableStateFlow(FavoritesUiState(items = listOf(movie)))
        every { viewModel.uiState } returns uiState

        // When
        composeTestRule.setContent {
            FavoritesRoute(
                onMovieSelected = onMovieSelected,
                onBackClick = onBackClick,
                viewModel = viewModel
            )
        }
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithText("Favorite Movie 1").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Favorite Movie 1").performClick()

        // Then
        verify { onMovieSelected(movie) }
    }

    @Test
    fun whenBackIsClicked_shouldCallOnBackClick() {
        // Given
        val uiState = MutableStateFlow(FavoritesUiState())
        every { viewModel.uiState } returns uiState
        lateinit var backButtonContentDescription: String

        // When
        composeTestRule.setContent {
            backButtonContentDescription = composeTestRule.activity.getString(R.string.favorite_screen_content_description_top_app_bar_back_button)
            FavoritesRoute(
                onMovieSelected = onMovieSelected,
                onBackClick = onBackClick,
                viewModel = viewModel
            )
        }
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithContentDescription(backButtonContentDescription).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithContentDescription(backButtonContentDescription).performClick()

        // Then
        verify { onBackClick() }
    }
}

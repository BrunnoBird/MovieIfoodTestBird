package com.example.movieifoodtest.presentation.favorites

import app.cash.turbine.test
import com.example.movieifoodtest.MainDispatcherRule
import com.example.movieifoodtest.domain.model.Movie
import com.example.movieifoodtest.domain.usecase.ObserveFavoritesUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class FavoritesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var observeFavorites: ObserveFavoritesUseCase
    private lateinit var viewModel: FavoritesViewModel

    @Before
    fun setUp() {
        observeFavorites = mockk<ObserveFavoritesUseCase>()
    }

    @Test
    fun `when observeFavorites emits movie list should update uiState with list`() = runTest {
        val mockMovie1 = mockk<Movie>()
        val mockMovie2 = mockk<Movie>()
        val mockMovieList = listOf(mockMovie1, mockMovie2)

        val expectedState = FavoritesUiState(items = mockMovieList)

        every { observeFavorites() } returns flowOf(mockMovieList)

        viewModel = FavoritesViewModel(observeFavorites)

        viewModel.uiState.test {
            assertEquals(expectedState, awaitItem())

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when observeFavorites emits empty list should update uiState with empty list`() = runTest {
        val emptyList = emptyList<Movie>()
        every { observeFavorites() } returns flowOf(emptyList)

        val expectedState = FavoritesUiState(items = emptyList())

        viewModel = FavoritesViewModel(observeFavorites)

        viewModel.uiState.test {
            assertEquals(expectedState, awaitItem())

            cancelAndConsumeRemainingEvents()
        }
    }
}
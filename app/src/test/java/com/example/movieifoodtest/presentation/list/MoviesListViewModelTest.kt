package com.example.movieifoodtest.presentation.list

import com.example.movieifoodtest.domain.model.DomainError
import com.example.movieifoodtest.domain.model.DomainResult
import com.example.movieifoodtest.domain.model.Movie
import com.example.movieifoodtest.domain.usecase.SearchMoviesUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MoviesListViewModelTest {

    @RelaxedMockK
    lateinit var searchMovies: SearchMoviesUseCase

    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onQueryChange updates state`() {
        val viewModel = MoviesListViewModel(searchMovies)

        viewModel.onQueryChange("Matrix")

        assertThat(viewModel.uiState.value.query).isEqualTo("Matrix")
    }

    @Test
    fun `search success updates items and clears error`() = runTest {
        val movies = listOf(Movie(1L, "Matrix", "Neo", null, 8.7))
        coEvery { searchMovies("matrix", 1) } returns DomainResult.success(movies)
        val viewModel = MoviesListViewModel(searchMovies)

        viewModel.onQueryChange("matrix")
        viewModel.search()

        val state = viewModel.uiState.value
        assertThat(state.loading).isFalse()
        assertThat(state.items).isEqualTo(movies)
        assertThat(state.error).isNull()
        coVerify(exactly = 1) { searchMovies("matrix", 1) }
    }

    @Test
    fun `search failure exposes error message`() = runTest {
        val message = "boom"
        coEvery { searchMovies("matrix", 1) } returns DomainResult.failure(
            DomainError.Unknown(message)
        )
        val viewModel = MoviesListViewModel(searchMovies)

        viewModel.onQueryChange("matrix")
        viewModel.search()

        val state = viewModel.uiState.value
        assertThat(state.loading).isFalse()
        assertThat(state.items).isEmpty()
        assertThat(state.error).isEqualTo("Unknown - $message")
        coVerify(exactly = 1) { searchMovies("matrix", 1) }
    }

    @Test
    fun `search uses trimmed query`() = runTest {
        coEvery { searchMovies("matrix", 1) } returns DomainResult.success(emptyList())
        val viewModel = MoviesListViewModel(searchMovies)

        viewModel.onQueryChange("  matrix  ")
        viewModel.search()

        coVerify(exactly = 1) { searchMovies("matrix", 1) }
    }
}
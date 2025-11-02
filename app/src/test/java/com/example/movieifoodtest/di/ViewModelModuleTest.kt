package com.example.movieifoodtest.di

import com.example.movieifoodtest.domain.model.DomainResult
import com.example.movieifoodtest.domain.model.Movie
import com.example.movieifoodtest.domain.usecase.GetMovieDetailsUseCase
import com.example.movieifoodtest.domain.usecase.ObserveFavoritesUseCase
import com.example.movieifoodtest.domain.usecase.SearchMoviesUseCase
import com.example.movieifoodtest.domain.usecase.ToggleFavoriteUseCase
import com.example.movieifoodtest.presentation.details.MovieDetailsViewModel
import com.example.movieifoodtest.presentation.favorites.FavoritesViewModel
import com.example.movieifoodtest.presentation.list.MoviesListViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ViewModelModuleTest {

    private lateinit var koinApplication: KoinApplication
    private lateinit var searchUseCase: SearchMoviesUseCase
    private lateinit var observeFavorites: ObserveFavoritesUseCase
    private lateinit var getMovieDetails: GetMovieDetailsUseCase
    private lateinit var toggleFavorite: ToggleFavoriteUseCase

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        stopKoin()
        searchUseCase = mockk()
        observeFavorites = mockk()
        getMovieDetails = mockk()
        toggleFavorite = mockk()

        val dependencies = module {
            single { searchUseCase }
            single { observeFavorites }
            single { getMovieDetails }
            single { toggleFavorite }
        }

        koinApplication = startKoin {
            modules(dependencies, viewModelModule)
        }
    }

    @After
    fun tearDown() {
        stopKoin()
        Dispatchers.resetMain()
    }

    @Test
    fun `movies list view model resolves and delegates to search use case`() =
        runTest(dispatcher.scheduler) {
            val expected = DomainResult.success(emptyList<Movie>())
            coEvery { searchUseCase.invoke("query", 1) } returns expected

            val viewModel = koinApplication.koin.get<MoviesListViewModel>()

            viewModel.onQueryChange("  query  ")
            viewModel.search()

            advanceUntilIdle()

            coVerify(exactly = 1) { searchUseCase.invoke("query", 1) }
        }

    @Test
    fun `favorites view model maps favorites flow`() = runTest(dispatcher.scheduler) {
        val favorites = listOf(
            Movie(id = 1L, title = "Favorite", overview = "", posterUrl = null, rating = 7.5)
        )
        every { observeFavorites.invoke() } returns flowOf(favorites)

        val viewModel = koinApplication.koin.get<FavoritesViewModel>()

        advanceUntilIdle()

        val state = viewModel.uiState.first { it.items.isNotEmpty() }

        assertThat(state.items).isEqualTo(favorites)
        verify(exactly = 1) { observeFavorites.invoke() }
    }

    @Test
    fun `movie details view model resolves with parameter and loads data`() =
        runTest(dispatcher.scheduler) {
            val movie =
                Movie(id = 5L, title = "Movie", overview = "", posterUrl = null, rating = 8.0)
            coEvery { getMovieDetails.invoke(5L) } returns DomainResult.success(movie)
            coEvery { toggleFavorite.invoke(movie) } returns DomainResult.success(true)

            val viewModel = koinApplication.koin.get<MovieDetailsViewModel> { parametersOf(5L) }

            advanceUntilIdle()

            coVerify(exactly = 1) { getMovieDetails.invoke(5L) }
            assertThat(viewModel.uiState.value.data).isEqualTo(movie)

            viewModel.onToggleFavorite()

            advanceUntilIdle()

            coVerify(exactly = 1) { toggleFavorite.invoke(movie) }
        }
}
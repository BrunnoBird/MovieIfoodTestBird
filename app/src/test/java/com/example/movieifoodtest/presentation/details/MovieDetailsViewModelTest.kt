package com.example.movieifoodtest.presentation.details

import com.example.movieifoodtest.domain.model.DomainError
import com.example.movieifoodtest.domain.model.DomainResult
import com.example.movieifoodtest.domain.model.Movie
import com.example.movieifoodtest.domain.usecase.GetMovieDetailsUseCase
import com.example.movieifoodtest.domain.usecase.ObserveFavoritesUseCase
import com.example.movieifoodtest.domain.usecase.ToggleFavoriteUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailsViewModelTest {

    @RelaxedMockK
    lateinit var getMovieDetails: GetMovieDetailsUseCase

    @RelaxedMockK
    lateinit var toggleFavorite: ToggleFavoriteUseCase

    @RelaxedMockK
    lateinit var observeFavorites: ObserveFavoritesUseCase

    private val dispatcher = UnconfinedTestDispatcher()
    private val favoritesFlow = MutableSharedFlow<List<Movie>>(replay = 0)

    private val movie = Movie(1L, "Matrix", "Neo", null, 8.7)

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)
        coEvery { observeFavorites() } returns favoritesFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): MovieDetailsViewModel {
        return MovieDetailsViewModel(1L, getMovieDetails, toggleFavorite, observeFavorites)
    }

    @Test
    fun `init loads details successfully`() = runTest {
        coEvery { getMovieDetails(1L) } returns DomainResult.success(movie)

        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertThat(state.loading).isFalse()
        assertThat(state.data).isEqualTo(movie)
        assertThat(state.error).isNull()
        coVerify(exactly = 1) { getMovieDetails(1L) }
    }

    @Test
    fun `init handles detail failure`() = runTest {
        coEvery { getMovieDetails(1L) } returns DomainResult.failure(
            DomainError.Unknown("error")
        )

        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertThat(state.loading).isFalse()
        assertThat(state.data).isNull()
        assertThat(state.error).isEqualTo("Unknown - error")
        coVerify(exactly = 1) { getMovieDetails(1L) }
    }

    @Test
    fun `retry triggers another load`() = runTest {
        coEvery { getMovieDetails(1L) } returnsMany listOf(
            DomainResult.failure(DomainError.Unknown("error")),
            DomainResult.success(movie)
        )

        val viewModel = createViewModel()
        assertThat(viewModel.uiState.value.error).isEqualTo("Unknown - error")

        viewModel.retry()

        val state = viewModel.uiState.value
        assertThat(state.loading).isFalse()
        assertThat(state.data).isEqualTo(movie)
        assertThat(state.error).isNull()
        coVerify(exactly = 2) { getMovieDetails(1L) }
    }

    @Test
    fun `toggle favorite success updates state`() = runTest {
        coEvery { getMovieDetails(1L) } returns DomainResult.success(movie)
        coEvery { toggleFavorite(movie) } returns DomainResult.success(true)

        val viewModel = createViewModel()

        viewModel.onToggleFavorite()

        val state = viewModel.uiState.value
        assertThat(state.isFavorite).isTrue()
        assertThat(state.toggleError).isNull()
        assertThat(state.favoriteChanged).isTrue()
        coVerify(exactly = 1) { toggleFavorite(movie) }
    }

    @Test
    fun `toggle favorite failure exposes error`() = runTest {
        coEvery { getMovieDetails(1L) } returns DomainResult.success(movie)
        coEvery { toggleFavorite(movie) } returns DomainResult.failure(
            DomainError.Unknown("fail")
        )

        val viewModel = createViewModel()

        viewModel.onToggleFavorite()

        val state = viewModel.uiState.value
        assertThat(state.toggleError).isEqualTo("Unknown - fail")
        assertThat(state.favoriteChanged).isNull()
        coVerify(exactly = 1) { toggleFavorite(movie) }
    }

    @Test
    fun `favorite change flag can be consumed`() = runTest {
        coEvery { getMovieDetails(1L) } returns DomainResult.success(movie)
        coEvery { toggleFavorite(movie) } returns DomainResult.success(false)

        val viewModel = createViewModel()

        viewModel.onToggleFavorite()
        assertThat(viewModel.uiState.value.favoriteChanged).isFalse()

        viewModel.onFavoriteChangeConsumed()

        assertThat(viewModel.uiState.value.favoriteChanged).isNull()
    }

    @Test
    fun `toggle error can be consumed`() = runTest {
        coEvery { getMovieDetails(1L) } returns DomainResult.success(movie)
        coEvery { toggleFavorite(movie) } returns DomainResult.failure(
            DomainError.Unknown("boom")
        )

        val viewModel = createViewModel()

        viewModel.onToggleFavorite()
        assertThat(viewModel.uiState.value.toggleError).isEqualTo("Unknown - boom")

        viewModel.onToggleErrorConsumed()

        assertThat(viewModel.uiState.value.toggleError).isNull()
    }

    @Test
    fun `favorites observation updates favorite flag`() = runTest {
        coEvery { getMovieDetails(1L) } returns DomainResult.success(movie)
        val another = Movie(2L, "Other", "", null, 5.0)

        val viewModel = createViewModel()

        favoritesFlow.emit(listOf(another))
        assertThat(viewModel.uiState.value.isFavorite).isFalse()

        favoritesFlow.emit(listOf(movie))
        assertThat(viewModel.uiState.value.isFavorite).isTrue()

        favoritesFlow.emit(emptyList())
        assertThat(viewModel.uiState.value.isFavorite).isFalse()
    }
}
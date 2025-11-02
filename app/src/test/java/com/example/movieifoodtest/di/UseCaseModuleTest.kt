package com.example.movieifoodtest.di

import com.example.movieifoodtest.domain.model.DomainResult
import com.example.movieifoodtest.domain.model.Movie
import com.example.movieifoodtest.domain.repository.MoviesRepository
import com.example.movieifoodtest.domain.usecase.GetMovieDetailsUseCase
import com.example.movieifoodtest.domain.usecase.ObserveFavoritesUseCase
import com.example.movieifoodtest.domain.usecase.SearchMoviesUseCase
import com.example.movieifoodtest.domain.usecase.ToggleFavoriteUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class UseCaseModuleTest {

    private lateinit var koinApplication: KoinApplication
    private val repository: MoviesRepository = mockk(relaxed = true)

    @Before
    fun setup() {
        stopKoin()
        clearMocks(repository)
        val dependencies = module {
            single { repository }
        }
        koinApplication = startKoin {
            modules(dependencies, useCaseModule)
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `search movies use case trims query and delegates to repository`() = runTest {
        val useCase = koinApplication.koin.get<SearchMoviesUseCase>()
        val expected = DomainResult.success(emptyList<Movie>())
        coEvery { repository.search("query", 5) } returns expected

        val result = useCase("  query  ", page = 5)

        assertThat(result).isEqualTo(expected)
        coVerify(exactly = 1) { repository.search("query", 5) }
    }

    @Test
    fun `search movies use case avoids repository call when query blank`() = runTest {
        val useCase = koinApplication.koin.get<SearchMoviesUseCase>()

        val result = useCase("   ")

        assertThat(result).isEqualTo(DomainResult.success(emptyList<Movie>()))
        coVerify(exactly = 0) { repository.search(any(), any()) }
    }

    @Test
    fun `use case definitions are factories`() {
        val first = koinApplication.koin.get<SearchMoviesUseCase>()
        val second = koinApplication.koin.get<SearchMoviesUseCase>()

        assertThat(first).isNotSameInstanceAs(second)
    }

    @Test
    fun `get movie details use case delegates to repository`() = runTest {
        val useCase = koinApplication.koin.get<GetMovieDetailsUseCase>()
        val movie = Movie(id = 10L, title = "Title", overview = "", posterUrl = null, rating = 8.0)
        val expected = DomainResult.success(movie)
        coEvery { repository.details(10L) } returns expected

        val result = useCase(10L)

        assertThat(result).isEqualTo(expected)
        coVerify(exactly = 1) { repository.details(10L) }
    }

    @Test
    fun `toggle favorite use case delegates to repository`() = runTest {
        val useCase = koinApplication.koin.get<ToggleFavoriteUseCase>()
        val movie = Movie(id = 20L, title = "Title", overview = "", posterUrl = null, rating = 7.0)
        val expected = DomainResult.success(true)
        coEvery { repository.toggleFavorite(movie) } returns expected

        val result = useCase(movie)

        assertThat(result).isEqualTo(expected)
        coVerify(exactly = 1) { repository.toggleFavorite(movie) }
    }

    @Test
    fun `observe favorites use case delegates to repository flow`() = runTest {
        val useCase = koinApplication.koin.get<ObserveFavoritesUseCase>()
        val favorites = listOf(Movie(id = 1L, title = "Fav", overview = "", posterUrl = null, rating = 5.0))
        every { repository.observeFavorites() } returns flowOf(favorites)

        val result = useCase().first()

        assertThat(result).isEqualTo(favorites)
    }
}
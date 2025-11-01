package com.example.movieifoodtest.presentation.domain.usecase

import com.example.movieifoodtest.domain.model.DomainError
import com.example.movieifoodtest.domain.model.DomainException
import com.example.movieifoodtest.domain.model.DomainResult
import com.example.movieifoodtest.domain.model.Movie
import com.example.movieifoodtest.domain.model.exceptionOrNull
import com.example.movieifoodtest.domain.model.getOrThrow
import com.example.movieifoodtest.domain.repository.MoviesRepository
import com.example.movieifoodtest.domain.usecase.GetMovieDetailsUseCase
import com.example.movieifoodtest.domain.usecase.ObserveFavoritesUseCase
import com.example.movieifoodtest.domain.usecase.SearchMoviesUseCase
import com.example.movieifoodtest.domain.usecase.ToggleFavoriteUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UseCasesTest {

    @RelaxedMockK
    lateinit var repo: MoviesRepository

    private lateinit var searchUC: SearchMoviesUseCase
    private lateinit var detailsUC: GetMovieDetailsUseCase
    private lateinit var toggleFavUC: ToggleFavoriteUseCase
    private lateinit var observeFavsUC: ObserveFavoritesUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        searchUC = SearchMoviesUseCase(repo)
        detailsUC = GetMovieDetailsUseCase(repo)
        toggleFavUC = ToggleFavoriteUseCase(repo)
        observeFavsUC = ObserveFavoritesUseCase(repo)
    }

    // -------- SearchMoviesUseCase --------

    @Test
    fun `search returns success from repository`() = runTest {
        val list = listOf(Movie(1L, "Matrix", "Neo", null, 8.7))
        coEvery { repo.search("matrix", 1) } returns DomainResult.success(list)

        val result = searchUC("matrix", 1)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().size)
        coVerify(exactly = 1) { repo.search("matrix", 1) }
    }

    @Test
    fun `search propagates failure from repository`() = runTest {
        val messageError = "boom"
        val error = DomainException(DomainError.Unknown(messageError))
        coEvery { repo.search("x", 2) } returns DomainResult.failure(error)

        val result = searchUC("x", 2)

        assertTrue(result.isFailure)
        assertEquals("Unknown - $messageError", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { repo.search("x", 2) }
    }

    // -------- GetMovieDetailsUseCase --------

    @Test
    fun `details returns success from repository`() = runTest {
        val movie = Movie(10L, "Inception", "Dreams", null, 9.0)
        coEvery { repo.details(10L) } returns DomainResult.success(movie)

        val result = detailsUC(10L)

        assertTrue(result.isSuccess)
        assertEquals(10L, result.getOrThrow().id)
        coVerify(exactly = 1) { repo.details(10L) }
    }

    @Test
    fun `details propagates failure from repository`() = runTest {
        val err = DomainException(DomainError.NotFound)
        coEvery { repo.details(99L) } returns DomainResult.failure(err)

        val result = detailsUC(99L)

        assertEquals("NotFound", result.exceptionOrNull()?.message)
        assertEquals(DomainError.NotFound, result.exceptionOrNull()?.domain)
        coVerify(exactly = 1) { repo.details(99L) }
    }

    // -------- ToggleFavoriteUseCase --------

    @Test
    fun `toggleFavorite returns success from repository`() = runTest {
        val movie = Movie(1L, "T", "O", null, 7.0)
        coEvery { repo.toggleFavorite(movie) } returns DomainResult.success(true)

        val result = toggleFavUC(movie)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repo.toggleFavorite(movie) }
    }

    @Test
    fun `toggleFavorite propagates failure from repository`() = runTest {
        val movie = Movie(2L, "X", "Y", null, 5.0)
        val err = DomainException(DomainError.Unknown("db failed"))
        coEvery { repo.toggleFavorite(movie) } returns DomainResult.failure(err)

        val result = toggleFavUC(movie)

        assertTrue(result.isFailure)
        assertEquals(DomainError.Unknown("db failed"), result.exceptionOrNull()?.domain)
        coVerify(exactly = 1) { repo.toggleFavorite(movie) }
    }

    // -------- ObserveFavoritesUseCase --------

    @Test
    fun `observeFavorites emits values from repository`() = runTest {
        val flow = MutableStateFlow(listOf(Movie(3L, "A", "B", null, 6.0)))
        every { repo.observeFavorites() } returns flow

        val firstEmission = observeFavsUC().first()

        assertEquals(1, firstEmission.size)
        assertEquals(3L, firstEmission.first().id)
    }
}

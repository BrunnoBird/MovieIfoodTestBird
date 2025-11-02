package com.example.movieifoodtest.domain.usecase

import com.example.movieifoodtest.domain.model.DomainError
import com.example.movieifoodtest.domain.model.DomainException
import com.example.movieifoodtest.domain.model.DomainResult
import com.example.movieifoodtest.domain.model.Movie
import com.example.movieifoodtest.domain.model.exceptionOrNull
import com.example.movieifoodtest.domain.repository.MoviesRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ToggleFavoriteUseCaseTest {

    @RelaxedMockK
    lateinit var repo: MoviesRepository

    private lateinit var toggleFavUC: ToggleFavoriteUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        toggleFavUC = ToggleFavoriteUseCase(repo)
    }

    @Test
    fun `toggleFavorite returns success from repository`() = runTest {
        val movie = Movie(1L, "T", "O", null, 7.0)
        coEvery { repo.toggleFavorite(movie) } returns DomainResult.Companion.success(true)

        val result = toggleFavUC(movie)

        Assert.assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repo.toggleFavorite(movie) }
    }

    @Test
    fun `toggleFavorite propagates failure from repository`() = runTest {
        val movie = Movie(2L, "X", "Y", null, 5.0)
        coEvery { repo.toggleFavorite(movie) } returns DomainResult.failure(DomainError.Unknown("db failed"))

        val result = toggleFavUC(movie)

        Assert.assertTrue(result.isFailure)
        Assert.assertEquals(DomainError.Unknown("db failed"), result.exceptionOrNull()?.domain)
        coVerify(exactly = 1) { repo.toggleFavorite(movie) }
    }
}
package com.example.movieifoodtest.domain.usecase

import com.example.movieifoodtest.domain.model.DomainError
import com.example.movieifoodtest.domain.model.DomainException
import com.example.movieifoodtest.domain.model.DomainResult
import com.example.movieifoodtest.domain.model.Movie
import com.example.movieifoodtest.domain.model.exceptionOrNull
import com.example.movieifoodtest.domain.model.getOrThrow
import com.example.movieifoodtest.domain.repository.MoviesRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GetMovieDetailsUseCaseTest {

    @RelaxedMockK
    lateinit var repo: MoviesRepository

    private lateinit var detailsUC: GetMovieDetailsUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        detailsUC = GetMovieDetailsUseCase(repo)
    }

    @Test
    fun `details returns success from repository`() = runTest {
        val movie = Movie(10L, "Inception", "Dreams", null, 9.0)
        coEvery { repo.details(10L) } returns DomainResult.Companion.success(movie)

        val result = detailsUC(10L)

        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals(10L, result.getOrThrow().id)
        coVerify(exactly = 1) { repo.details(10L) }
    }

    @Test
    fun `details propagates failure from repository`() = runTest {
        val err = DomainException(DomainError.NotFound)
        coEvery { repo.details(99L) } returns DomainResult.Companion.failure(err)

        val result = detailsUC(99L)

        Assert.assertEquals("NotFound", result.exceptionOrNull()?.message)
        Assert.assertEquals(DomainError.NotFound, result.exceptionOrNull()?.domain)
        coVerify(exactly = 1) { repo.details(99L) }
    }
}
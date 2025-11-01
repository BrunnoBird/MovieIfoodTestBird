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

class SearchMoviesUseCaseTest {

    @RelaxedMockK
    lateinit var repo: MoviesRepository

    private lateinit var searchUC: SearchMoviesUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        searchUC = SearchMoviesUseCase(repo)
    }

    @Test
    fun `search returns success from repository`() = runTest {
        val list = listOf(Movie(1L, "Matrix", "Neo", null, 8.7))
        coEvery { repo.search("matrix", 1) } returns DomainResult.Companion.success(list)

        val result = searchUC("matrix", 1)

        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals(1, result.getOrThrow().size)
        coVerify(exactly = 1) { repo.search("matrix", 1) }
    }

    @Test
    fun `search propagates failure from repository`() = runTest {
        val messageError = "boom"
        val error = DomainException(DomainError.Unknown(messageError))
        coEvery { repo.search("x", 2) } returns DomainResult.Companion.failure(error)

        val result = searchUC("x", 2)

        Assert.assertTrue(result.isFailure)
        Assert.assertEquals("Unknown - $messageError", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { repo.search("x", 2) }
    }
}
package com.example.movieifoodtest.domain.usecase

import com.example.movieifoodtest.domain.model.Movie
import com.example.movieifoodtest.domain.repository.MoviesRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ObserveFavoritesUseCaseTest {

    @RelaxedMockK
    lateinit var repo: MoviesRepository

    private lateinit var observeFavsUC: ObserveFavoritesUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        observeFavsUC = ObserveFavoritesUseCase(repo)
    }

    @Test
    fun `observeFavorites emits values from repository`() = runTest {
        val flow = MutableStateFlow(listOf(Movie(3L, "A", "B", null, 6.0)))
        every { repo.observeFavorites() } returns flow

        val firstEmission = observeFavsUC().first()

        Assert.assertEquals(1, firstEmission.size)
        Assert.assertEquals(3L, firstEmission.first().id)
    }
}
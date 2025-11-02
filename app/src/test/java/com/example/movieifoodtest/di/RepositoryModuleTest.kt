package com.example.movieifoodtest.di

import com.example.movieifoodtest.data.database.FavoriteDao
import com.example.movieifoodtest.data.network.tmdb.TmdbApi
import com.example.movieifoodtest.data.network.tmdb.dto.MovieDto
import com.example.movieifoodtest.data.network.tmdb.dto.PagedResponse
import com.example.movieifoodtest.data.repository.MoviesRepositoryImpl
import com.example.movieifoodtest.domain.model.DomainResult
import com.example.movieifoodtest.domain.repository.MoviesRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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
class RepositoryModuleTest {

    private lateinit var koinApplication: KoinApplication
    private val api: TmdbApi = mockk()
    private val dao: FavoriteDao = mockk(relaxed = true)

    @Before
    fun setup() {
        stopKoin()
        val dependencies = module {
            single { api }
            single { dao }
        }
        koinApplication = startKoin {
            modules(dependencies, repositoryModule)
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `repository module binds interface to implementation`() {
        val repository = koinApplication.koin.get<MoviesRepository>()

        assertThat(repository).isInstanceOf(MoviesRepositoryImpl::class.java)
    }

    @Test
    fun `repository from module delegates search to provided api`() = runTest {
        val repository = koinApplication.koin.get<MoviesRepository>()
        val response = PagedResponse(
            page = 1,
            results = listOf(MovieDto(id = 1L, title = "Movie"))
        )
        coEvery { api.searchMovies("query", 2) } returns response

        val result = repository.search("query", 2)

        assertThat(result).isInstanceOf(DomainResult.Success::class.java)
        coVerify(exactly = 1) { api.searchMovies("query", 2) }
    }
}
package com.example.movieifoodtest.data.repository

import com.example.movieifoodtest.data.database.FavoriteDao
import com.example.movieifoodtest.data.database.FavoriteMovieEntity
import com.example.movieifoodtest.data.network.tmdb.TmdbApi
import com.example.movieifoodtest.data.network.tmdb.dto.MovieDto
import com.example.movieifoodtest.data.network.tmdb.dto.PagedResponse
import com.example.movieifoodtest.domain.model.DomainError
import com.example.movieifoodtest.domain.model.DomainException
import com.example.movieifoodtest.domain.model.Movie
import com.example.movieifoodtest.domain.model.exceptionOrNull
import com.example.movieifoodtest.domain.model.getOrThrow
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class MoviesRepositoryImplTest {

    private lateinit var api: TmdbApi
    private lateinit var dao: FavoriteDao
    private lateinit var repo: MoviesRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        api = mockk()
        dao = mockk(relaxed = true)
        repo = MoviesRepositoryImpl(api = api, dao = dao)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `search returns success when API succeeds`() = runTest {
        // Arrange
        val dto = MovieDto(
            id = 603L,
            title = "The Matrix",
            overview = "Neo discovers the Matrix",
            posterPath = "/poster.jpg",
            voteAverage = 8.7
        )
        coEvery { api.searchMovies("matrix", 1) } returns
                PagedResponse(page = 1, results = listOf(dto))

        // Act
        val result = repo.search("matrix", 1)

        // Assert
        Assert.assertTrue(result.isSuccess)
        val list = result.getOrThrow()
        Assert.assertEquals(1, list.size)
        val movie = list.first()
        Assert.assertEquals(603L, movie.id)
        Assert.assertEquals("The Matrix", movie.title)
        coVerify(exactly = 1) { api.searchMovies("matrix", 1) }
    }

    @Test
    fun `details returns success when API returns movie`() = runTest {
        val dto = MovieDto(
            id = 10L,
            title = "Inception",
            overview = "Dreams",
            posterPath = "/p.jpg",
            voteAverage = 9.0
        )
        coEvery { api.getMovieDetails(10L) } returns dto

        val result = repo.details(10L)

        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals(10L, result.getOrThrow().id)
        coVerify(exactly = 1) { api.getMovieDetails(10L) }
    }

    @Test
    fun `toggleFavorite adds when movie not yet favorite`() = runTest {
        val movie = Movie(
            id = 1L,
            title = "T",
            overview = "O",
            posterUrl = "u",
            rating = 9.0
        )
        coEvery { dao.exists(1L) } returns false
        coEvery { dao.upsert(any()) } just Runs

        val result = repo.toggleFavorite(movie)

        Assert.assertTrue(result.isSuccess)
        Assert.assertTrue(result.getOrThrow())
        coVerify(exactly = 1) {
            dao.upsert(match { it.id == 1L && it.title == "T" && it.rating == 9.0 })
        }
    }

    @Test
    fun `toggleFavorite removes when movie already favorite`() = runTest {
        val movie = Movie(
            id = 2L,
            title = "Existing",
            overview = "O",
            posterUrl = "u",
            rating = 8.0
        )
        coEvery { dao.exists(2L) } returns true
        coEvery { dao.deleteById(2L) } just Runs

        val result = repo.toggleFavorite(movie)

        Assert.assertTrue(result.isSuccess)
        Assert.assertFalse(result.getOrThrow())
        coVerify(exactly = 1) { dao.deleteById(2L) }
    }

    @Test
    fun `observeFavorites maps flow correctly`() = runTest {
        val flow = MutableStateFlow(
            listOf(FavoriteMovieEntity(1L, "T", "O", "U", 7.0))
        )
        every { dao.observeAll() } returns flow

        val emission = repo.observeFavorites().first()

        Assert.assertEquals(1, emission.size)
        Assert.assertEquals(1L, emission.first().id)
        Assert.assertEquals("T", emission.first().title)
        verify(exactly = 1) { dao.observeAll() }
    }

    @Test
    fun `search returns failure DomainError_Unauthorized when API throws Http 401`() = runTest {
        // Cria um HttpException(401)
        val errorBody = """{"status_code":7,"status_message":"Invalid API key"}"""
            .toResponseBody("application/json".toMediaType())
        val response = Response.error<PagedResponse<MovieDto>>(401, errorBody)
        val http401 = HttpException(response)

        coEvery { api.searchMovies(any(), any()) } throws http401

        val result = repo.search("x", 1)

        Assert.assertTrue(result.isFailure)
        val ex = result.exceptionOrNull()
        Assert.assertNotNull(ex)
        Assert.assertEquals(DomainError.Unauthorized, ex?.domain)
        coVerify(exactly = 1) { api.searchMovies("x", 1) }
    }

    @Test
    fun `search returns failure when API throws runtime exception`() = runTest {
        coEvery { api.searchMovies(any(), any()) } throws RuntimeException("boom")

        val result = repo.search("any", 1)

        Assert.assertTrue(result.isFailure)
        val ex = result.exceptionOrNull()
        Assert.assertNotNull(ex)
        Assert.assertTrue(ex is DomainException)
        Assert.assertEquals(DomainError.Unknown("boom"), ex?.domain)
        coVerify(exactly = 1) { api.searchMovies("any", 1) }
    }
}
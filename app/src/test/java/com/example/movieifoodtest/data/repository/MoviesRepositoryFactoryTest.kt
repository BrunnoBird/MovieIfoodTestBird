package com.example.movieifoodtest.data.repository

import com.example.movieifoodtest.data.database.FavoriteDao
import com.example.movieifoodtest.data.network.tmdb.TmdbApi
import io.mockk.mockk
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class MoviesRepositoryFactoryTest {

    private val api: TmdbApi = mockk()
    private val dao: FavoriteDao = mockk()

    @Test
    fun `createMoviesRepository returns MoviesRepositoryImpl with provided dependencies`() {
        val repository = createMoviesRepository(api = api, dao = dao)

        assertTrue(repository is MoviesRepositoryImpl)

        val impl = repository as MoviesRepositoryImpl
        val apiField = MoviesRepositoryImpl::class.java.getDeclaredField("api").apply {
            isAccessible = true
        }
        val daoField = MoviesRepositoryImpl::class.java.getDeclaredField("dao").apply {
            isAccessible = true
        }

        assertSame(api, apiField.get(impl))
        assertSame(dao, daoField.get(impl))
    }
}
package com.example.movieifoodtest.feature.data

import com.example.movieifoodtest.core.database.FavoriteMovieEntity
import com.example.movieifoodtest.feature.movies.domain.Movie
import com.example.movieifoodtest.core.network.tmdb.dto.MovieDto
import com.example.movieifoodtest.feature.movies.data.toDomain
import com.example.movieifoodtest.feature.movies.data.toEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MappersTest {

    @Test
    fun `MovieDto toDomain maps all fields`() {
        val dto = MovieDto(
            id = 603L,
            title = "The Matrix",
            overview = "Neo discovers the Matrix",
            posterPath = "/poster.jpg",
            voteAverage = 8.7
        )
        val m = dto.toDomain()
        assertEquals(603L, m.id)
        assertEquals("The Matrix", m.title)
        assertEquals("Neo discovers the Matrix", m.overview)
        requireNotNull(m.posterUrl)
        assert(m.posterUrl.endsWith("/w500/poster.jpg") || m.posterUrl.endsWith("w500/poster.jpg"))
        assertEquals(8.7, m.rating, 0.0)
    }

    @Test
    fun `MovieDto toDomain handles nulls`() {
        val dto = MovieDto(
            id = 1L,
            title = null,
            overview = null,
            posterPath = null,
            voteAverage = null
        )
        val movie = dto.toDomain()
        assertEquals(1L, movie.id)
        assertEquals("", movie.title)
        assertEquals("", movie.overview)
        assertNull(movie.posterUrl)
        assertEquals(0.0, movie.rating, 0.0)
    }

    @Test
    fun `FavoriteMovieEntity toDomain maps correctly`() {
        val entity = FavoriteMovieEntity(
            id = 10L,
            title = "Title",
            overview = "Overview",
            posterUrl = "http://img",
            rating = 7.5
        )
        val movie = entity.toDomain()
        assertEquals(10L, movie.id)
        assertEquals("Title", movie.title)
        assertEquals("Overview", movie.overview)
        assertEquals("http://img", movie.posterUrl)
        assertEquals(7.5, movie.rating, 0.0)
    }

    @Test
    fun `Movie toEntity maps correctly`() {
        val movies = Movie(
            id = 11L,
            title = "T",
            overview = "O",
            posterUrl = "u",
            rating = 9.0
        )
        val e = movies.toEntity()
        assertEquals(11L, e.id)
        assertEquals("T", e.title)
        assertEquals("O", e.overview)
        assertEquals("u", e.posterUrl)
        assertEquals(9.0, e.rating, 0.0)
    }
}

package com.example.movieifoodtest.data.mapper

import com.example.movieifoodtest.data.database.FavoriteMovieEntity
import com.example.movieifoodtest.data.network.tmdb.dto.MovieDto
import com.example.movieifoodtest.domain.model.Movie
import org.junit.Assert
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
        Assert.assertEquals(603L, m.id)
        Assert.assertEquals("The Matrix", m.title)
        Assert.assertEquals("Neo discovers the Matrix", m.overview)
        requireNotNull(m.posterUrl)
        assert(m.posterUrl.endsWith("/w500/poster.jpg") || m.posterUrl.endsWith("w500/poster.jpg"))
        Assert.assertEquals(8.7, m.rating, 0.0)
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
        Assert.assertEquals(1L, movie.id)
        Assert.assertEquals("", movie.title)
        Assert.assertEquals("", movie.overview)
        Assert.assertNull(movie.posterUrl)
        Assert.assertEquals(0.0, movie.rating, 0.0)
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
        Assert.assertEquals(10L, movie.id)
        Assert.assertEquals("Title", movie.title)
        Assert.assertEquals("Overview", movie.overview)
        Assert.assertEquals("http://img", movie.posterUrl)
        Assert.assertEquals(7.5, movie.rating, 0.0)
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
        Assert.assertEquals(11L, e.id)
        Assert.assertEquals("T", e.title)
        Assert.assertEquals("O", e.overview)
        Assert.assertEquals("u", e.posterUrl)
        Assert.assertEquals(9.0, e.rating, 0.0)
    }
}
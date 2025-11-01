package com.example.movieifoodtest.data.mapper

import com.example.movieifoodtest.data.database.FavoriteMovieEntity
import com.example.movieifoodtest.data.network.tmdb.ImageUrlBuilder
import com.example.movieifoodtest.data.network.tmdb.dto.MovieDto
import com.example.movieifoodtest.domain.model.Movie

fun MovieDto.toDomain(): Movie = Movie(
    id = id,
    title = title.orEmpty(),
    overview = overview.orEmpty(),
    posterUrl = ImageUrlBuilder.buildPosterUrl(posterPath, "w500"),
    rating = voteAverage ?: 0.0
)

fun FavoriteMovieEntity.toDomain(): Movie = Movie(
    id = id,
    title = title,
    overview = overview,
    posterUrl = posterUrl,
    rating = rating
)

fun Movie.toEntity(): FavoriteMovieEntity = FavoriteMovieEntity(
    id = id,
    title = title,
    overview = overview,
    posterUrl = posterUrl,
    rating = rating
)

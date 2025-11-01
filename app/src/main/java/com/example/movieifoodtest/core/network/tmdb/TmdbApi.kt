package com.example.movieifoodtest.core.network.tmdb

import com.example.movieifoodtest.core.network.tmdb.dto.MovieDto
import com.example.movieifoodtest.core.network.tmdb.dto.PagedResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int
    ): PagedResponse<MovieDto>

    @GET("movie/{id}")
    suspend fun getMovieDetails(
        @Path("id") id: Long
    ): MovieDto
}
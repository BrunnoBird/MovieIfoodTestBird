package com.example.movieifoodtest.domain.repository

import com.example.movieifoodtest.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MoviesRepository {
    suspend fun search(query: String, page: Int): Result<List<Movie>>
    suspend fun details(id: Long): Result<Movie>
    suspend fun toggleFavorite(movie: Movie): Result<Unit>
    fun observeFavorites(): Flow<List<Movie>>
}
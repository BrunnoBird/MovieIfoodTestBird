package com.example.movieifoodtest.domain.repository

import com.example.movieifoodtest.domain.model.DomainResult
import com.example.movieifoodtest.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MoviesRepository {
    suspend fun search(query: String, page: Int): DomainResult<List<Movie>>
    suspend fun details(id: Long): DomainResult<Movie>
    suspend fun toggleFavorite(movie: Movie): DomainResult<Boolean>
    fun observeFavorites(): Flow<List<Movie>>
}
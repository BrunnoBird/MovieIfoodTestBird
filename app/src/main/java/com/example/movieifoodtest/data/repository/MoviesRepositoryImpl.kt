package com.example.movieifoodtest.data.repository

import com.example.movieifoodtest.data.database.FavoriteDao
import com.example.movieifoodtest.data.mapper.domainResultCatching
import com.example.movieifoodtest.data.mapper.toDomain
import com.example.movieifoodtest.data.mapper.toEntity
import com.example.movieifoodtest.data.network.tmdb.TmdbApi
import com.example.movieifoodtest.domain.model.DomainResult
import com.example.movieifoodtest.domain.model.Movie
import com.example.movieifoodtest.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MoviesRepositoryImpl(
    private val api: TmdbApi,
    private val dao: FavoriteDao
) : MoviesRepository {

    override suspend fun search(query: String, page: Int): DomainResult<List<Movie>> =
        domainResultCatching {
            api.searchMovies(query, page).results.map { it.toDomain() }
        }

    override suspend fun details(id: Long): DomainResult<Movie> =
        domainResultCatching {
            api.getMovieDetails(id).toDomain()
        }

    override suspend fun toggleFavorite(movie: Movie): DomainResult<Boolean> =
        domainResultCatching {
            val isFavorite = dao.exists(movie.id)
            if (isFavorite) {
                dao.deleteById(movie.id)
                false
            } else {
                dao.upsert(movie.toEntity())
                true
            }
        }

    override fun observeFavorites(): Flow<List<Movie>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }
}
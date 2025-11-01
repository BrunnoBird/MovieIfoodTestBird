package com.example.movieifoodtest.domain.usecase

import com.example.movieifoodtest.domain.model.Movie
import com.example.movieifoodtest.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow

class ObserveFavoritesUseCase(
    private val repo: MoviesRepository
) {
    operator fun invoke(): Flow<List<Movie>> = repo.observeFavorites()
}

package com.example.movieifoodtest.domain.usecase

import com.example.movieifoodtest.domain.model.DomainResult
import com.example.movieifoodtest.domain.model.Movie
import com.example.movieifoodtest.domain.repository.MoviesRepository

class ToggleFavoriteUseCase(
    private val repo: MoviesRepository
) {
    suspend operator fun invoke(movie: Movie): DomainResult<Unit> = repo.toggleFavorite(movie)
}

package com.example.movieifoodtest.domain.usecase

import com.example.movieifoodtest.domain.model.Movie
import com.example.movieifoodtest.domain.repository.MoviesRepository

class GetMovieDetailsUseCase(
    private val repo: MoviesRepository
) {
    suspend operator fun invoke(id: Long): Result<Movie> = repo.details(id)
}

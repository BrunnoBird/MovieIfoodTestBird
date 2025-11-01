package com.example.movieifoodtest.domain.usecase

import com.example.movieifoodtest.domain.model.Movie
import com.example.movieifoodtest.data.repository.MoviesRepository

class SearchMoviesUseCase(
    private val repo: MoviesRepository
) {
    suspend operator fun invoke(query: String, page: Int = 1): Result<List<Movie>> {
        val q = query.trim()
        if (q.isEmpty()) return Result.success(emptyList())
        return repo.search(q, page)
    }
}

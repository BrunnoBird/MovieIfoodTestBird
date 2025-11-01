package com.example.movieifoodtest.domain.usecase

import com.example.movieifoodtest.domain.model.DomainResult
import com.example.movieifoodtest.domain.model.Movie
import com.example.movieifoodtest.domain.repository.MoviesRepository

class SearchMoviesUseCase(
    private val repo: MoviesRepository
) {
    suspend operator fun invoke(query: String, page: Int = 1): DomainResult<List<Movie>> {
        val q = query.trim()
        if (q.isEmpty()) return DomainResult.success(emptyList())
        return repo.search(q, page)
    }
}

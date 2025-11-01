package com.example.movieifoodtest.data.repository

import com.example.movieifoodtest.data.database.FavoriteDao
import com.example.movieifoodtest.data.network.tmdb.TmdbApi
import com.example.movieifoodtest.domain.repository.MoviesRepository

fun createMoviesRepository(
    api: TmdbApi,
    dao: FavoriteDao,
): MoviesRepository = MoviesRepositoryImpl(api = api, dao = dao)

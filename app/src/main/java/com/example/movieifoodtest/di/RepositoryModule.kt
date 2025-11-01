package com.example.movieifoodtest.di

import com.example.movieifoodtest.data.repository.createMoviesRepository
import com.example.movieifoodtest.domain.repository.MoviesRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<MoviesRepository> { createMoviesRepository(api = get(), dao = get()) }
}
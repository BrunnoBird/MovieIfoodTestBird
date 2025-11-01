package com.example.movieifoodtest.di

import com.example.movieifoodtest.domain.repository.MoviesRepository
import com.example.movieifoodtest.data.repository.MoviesRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<MoviesRepository> { MoviesRepositoryImpl(api = get(), dao = get()) }
}
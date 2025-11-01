package com.example.movieifoodtest.di

import com.example.movieifoodtest.domain.usecase.GetMovieDetailsUseCase
import com.example.movieifoodtest.domain.usecase.ObserveFavoritesUseCase
import com.example.movieifoodtest.domain.usecase.SearchMoviesUseCase
import com.example.movieifoodtest.domain.usecase.ToggleFavoriteUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule = module {
    factoryOf(::SearchMoviesUseCase)
    factoryOf(::GetMovieDetailsUseCase)
    factoryOf(::ToggleFavoriteUseCase)
    factoryOf(::ObserveFavoritesUseCase)
}

package com.example.movieifoodtest.di

import com.example.movieifoodtest.presentation.details.MovieDetailsViewModel
import com.example.movieifoodtest.presentation.favorites.FavoritesViewModel
import com.example.movieifoodtest.presentation.list.MoviesListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val viewModelModule = module {
    viewModelOf(::MoviesListViewModel)
    viewModelOf(::FavoritesViewModel)
    viewModel { (id: Long) ->
        MovieDetailsViewModel(
            id = id,
            details = get(),
            toggleFavorite = get(),
            observeFavorites = get()
        )
    }
}

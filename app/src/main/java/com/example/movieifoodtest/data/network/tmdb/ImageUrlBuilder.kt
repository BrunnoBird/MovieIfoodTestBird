package com.example.movieifoodtest.data.network.tmdb

import com.example.movieifoodtest.BuildConfig

private const val SIZE_POSTER_IMAGE = "w500"

object ImageUrlBuilder {
    fun buildPosterUrl(posterPath: String?, size: String = SIZE_POSTER_IMAGE): String? {
        if (posterPath.isNullOrBlank()) return null
        return BuildConfig.TMDB_IMG_BASE + size + posterPath
    }
}

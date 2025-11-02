package com.example.movieifoodtest.navigation.data.screens

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class SearchScreen(val movieId: Long? = null) : NavKey
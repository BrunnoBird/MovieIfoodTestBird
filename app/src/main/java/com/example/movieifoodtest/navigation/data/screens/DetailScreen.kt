package com.example.movieifoodtest.navigation.data.screens

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class DetailScreen(
    val id: Long,
    val onDismiss: () -> Unit = {},
    val onToggleSuccess: () -> Unit = {},
    val onToggleFailure: () -> Unit = {}
) : NavKey
package com.example.movieifoodtest.navigation.data.callbacks

data class DetailCallbacks(
    val onDismiss: () -> Unit,
    val onToggleSuccess: (Boolean) -> Unit,
    val onToggleFailure: (String) -> Unit,
)
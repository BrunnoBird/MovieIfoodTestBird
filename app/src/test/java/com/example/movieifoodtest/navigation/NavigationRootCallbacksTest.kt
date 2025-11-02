package com.example.movieifoodtest.navigation

import com.example.movieifoodtest.domain.model.Movie
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NavigationRootCallbacksTest {

    private val movie = Movie(
        id = 123L,
        title = "Title",
        overview = "Overview",
        posterUrl = null,
        rating = 7.5,
    )

    @Test
    fun `search callbacks forward actions to detail and favorites destinations`() {
        val events = mutableListOf<String>()

        val callbacks = createSearchCallbacks(
            onNavigateToDetail = { id -> events += "detail:$id" },
            onNavigateToFavorites = { events += "favorites" },
        )

        callbacks.onMovieSelected(movie)
        callbacks.onFavoriteClick()

        assertThat(events)
            .containsExactly("detail:${movie.id}", "favorites")
            .inOrder()
    }

    @Test
    fun `favorites callbacks forward selections and back actions`() {
        val events = mutableListOf<String>()

        val callbacks = createFavoritesCallbacks(
            onNavigateToDetail = { id -> events += "detail:$id" },
            onBack = { events += "back" },
        )

        callbacks.onMovieSelected(movie)
        callbacks.onBackClick()

        assertThat(events)
            .containsExactly("detail:${movie.id}", "back")
            .inOrder()
    }

    @Test
    fun `detail callbacks close entry before reporting success`() {
        val events = mutableListOf<String>()

        val callbacks = createDetailCallbacks(
            onClose = { events += "close" },
            onToggleSucceeded = { events += "success" },
            onToggleFailed = { message -> events += "failure:$message" },
        )

        callbacks.onToggleSuccess(true)

        assertThat(events)
            .containsExactly("close", "success")
            .inOrder()
    }

    @Test
    fun `detail callbacks close entry before reporting failure`() {
        val events = mutableListOf<String>()

        val callbacks = createDetailCallbacks(
            onClose = { events += "close" },
            onToggleSucceeded = { events += "success" },
            onToggleFailed = { message -> events += "failure:$message" },
        )

        callbacks.onToggleFailure("boom")

        assertThat(events)
            .containsExactly("close", "failure:boom")
            .inOrder()
    }

    @Test
    fun `detail callbacks dismiss handler closes entry`() {
        val events = mutableListOf<String>()

        val callbacks = createDetailCallbacks(
            onClose = { events += "close" },
            onToggleSucceeded = {},
            onToggleFailed = {},
        )

        callbacks.onDismiss()

        assertThat(events)
            .containsExactly("close")
            .inOrder()
    }
}
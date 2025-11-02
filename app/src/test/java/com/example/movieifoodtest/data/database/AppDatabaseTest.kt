package com.example.movieifoodtest.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class AppDatabaseTest {

    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var dao: FavoriteDao

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.favoriteDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun favoriteDao_performsBasicCrudOperations() = runTest {
        val movie = FavoriteMovieEntity(
            id = 1L,
            title = "The Matrix",
            overview = "Neo discovers the truth about his world.",
            posterUrl = null,
            rating = 8.7
        )

        assertThat(dao.observeAll().first()).isEmpty()

        dao.upsert(movie)

        assertThat(dao.observeAll().first()).containsExactly(movie)
        assertThat(dao.exists(movie.id)).isTrue()

        val updated = movie.copy(rating = 9.0)
        dao.upsert(updated)

        assertThat(dao.observeAll().first()).containsExactly(updated)

        dao.deleteById(movie.id)

        assertThat(dao.observeAll().first()).isEmpty()
        assertThat(dao.exists(movie.id)).isFalse()
    }
}

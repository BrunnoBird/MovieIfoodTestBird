package com.example.movieifoodtest.di

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.movieifoodtest.data.database.AppDatabase
import com.example.movieifoodtest.data.database.FavoriteDao
import com.example.movieifoodtest.data.database.FavoriteMovieEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class DatabaseModuleTest {

    private lateinit var koinApplication: KoinApplication

    @Before
    fun setup() {
        stopKoin()
        val contextModule = module {
            single<Context> { ApplicationProvider.getApplicationContext() }
        }
        koinApplication = startKoin {
            modules(contextModule, databaseModule)
        }
    }

    @After
    fun tearDown() {
        koinApplication.koin.get<AppDatabase>().close()
        stopKoin()
    }

    @Test
    fun `database module provides persistent AppDatabase`() {
        val database = koinApplication.koin.get<AppDatabase>()

        assertThat(database.openHelper.databaseName).isEqualTo("movies.db")
    }

    @Test
    fun `favorite dao resolves from module and operates on the database`() = runTest {
        val dao = koinApplication.koin.get<FavoriteDao>()
        val entity = FavoriteMovieEntity(
            id = 123L,
            title = "Test Movie",
            overview = "Overview",
            posterUrl = null,
            rating = 7.5
        )

        dao.upsert(entity)

        assertThat(dao.exists(entity.id)).isTrue()

        dao.deleteById(entity.id)

        assertThat(dao.exists(entity.id)).isFalse()
    }
}
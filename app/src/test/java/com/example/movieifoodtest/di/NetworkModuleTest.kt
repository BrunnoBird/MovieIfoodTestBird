package com.example.movieifoodtest.di

import com.example.movieifoodtest.BuildConfig
import com.example.movieifoodtest.data.network.tmdb.TmdbApi
import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Retrofit

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class NetworkModuleTest {

    private lateinit var koinApplication: KoinApplication

    @Before
    fun setup() {
        stopKoin()
        koinApplication = startKoin {
            modules(networkModule)
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun `json factory uses resilient configuration`() {
        val json = koinApplication.koin.get<Json>()

        assertThat(json.configuration.ignoreUnknownKeys).isTrue()
        assertThat(json.configuration.coerceInputValues).isTrue()
        assertThat(json.configuration.explicitNulls).isFalse()
        assertThat(json.configuration.isLenient).isTrue()
    }

    @Test
    fun `okhttp client contains auth and logging interceptors`() {
        val client = koinApplication.koin.get<OkHttpClient>()

        assertThat(client.interceptors).hasSize(2)
        assertThat(client.interceptors.any { it is HttpLoggingInterceptor }).isTrue()
        assertThat(client.interceptors.any { it !is HttpLoggingInterceptor }).isTrue()
    }

    @Test
    fun `retrofit is configured with tmdb base url and shared client`() {
        val retrofit = koinApplication.koin.get<Retrofit>()
        val client = koinApplication.koin.get<OkHttpClient>()

        assertThat(retrofit.baseUrl().toString()).isEqualTo(BuildConfig.TMDB_BASE_URL)
        assertThat(retrofit.callFactory()).isSameInstanceAs(client)
    }

    @Test
    fun `tmdb api is created from retrofit`() {
        val api = koinApplication.koin.get<TmdbApi>()

        assertThat(api).isNotNull()
    }
}
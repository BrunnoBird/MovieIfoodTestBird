package com.example.movieifoodtest.data.network.tmdb

import com.example.movieifoodtest.data.network.http.createJson
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class TmdbApiTest {

    private lateinit var server: MockWebServer
    private lateinit var api: TmdbApi

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        val json: Json = createJson()
        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/").toString())
            .client(OkHttpClient())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        api = retrofit.create(TmdbApi::class.java)
    }

    @After
    fun tearDown() {
        server.close()
    }

    @Test
    fun `searchMovies parses successful response`() = runTest {
        val body = """
        {
          "page": 1,
          "results": [{
            "id": 603,
            "title": "The Matrix",
            "overview": "Neo discovers the Matrix",
            "poster_path": "/poster.jpg",
            "vote_average": 8.7
          }]
        }
        """.trimIndent()

        server.enqueue(
            MockResponse(
                code = 200,
                body = body
            )
        )

        val resp = api.searchMovies(query = "matrix", page = 1)
        assertEquals(1, resp.page)
        assertEquals(1, resp.results.size)
        val m = resp.results.first()
        assertEquals(603, m.id)
        assertEquals("The Matrix", m.title)
        assertEquals("/poster.jpg", m.posterPath)
        assertEquals(8.7, m.voteAverage!!, 0.0)
    }

    @Test(expected = HttpException::class)
    fun `searchMovies throws HttpException on 401`() = runTest {
        server.enqueue(
            MockResponse(
                code = 401,
                body = """{"status_code":7,"status_message":"Invalid API key"}"""
            )
        )
        api.searchMovies(query = "x", page = 1)
    }
}

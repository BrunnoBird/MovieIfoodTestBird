package com.example.movieifoodtest.core.network.http

import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AuthInterceptorsTest {

    private lateinit var server: MockWebServer
    private lateinit var client: OkHttpClient


    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        client = OkHttpClient.Builder()
            .addInterceptor(
                provideAuthInterceptor(
                    bearer = "BEARER_TOKEN",
                    apiKey = "API_KEY_V3"
                )
            )
            .build()
    }

    @After
    fun tearDown() {
        server.close()
    }

    @Test
    fun `adds Authorization header and api_key query param`() = runTest {
        server.enqueue(
            MockResponse(
                body = """{"ok":true}"""
            )
        )

        val request = Request.Builder()
            .url(server.url("/search/movie?query=matrix&page=1").toString())
            .build()

        client.newCall(request).execute().use { response ->
            assertEquals(200, response.code)
        }

        val recorded = server.takeRequest()
        assertEquals("Bearer BEARER_TOKEN", recorded.headers["Authorization"])
        assertEquals("/search/movie", recorded.url.encodedPath)
        assertEquals("API_KEY_V3", recorded.url.queryParameter("api_key"))
    }
}

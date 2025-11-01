package com.example.movieifoodtest.core.network.http

import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.http.GET
import java.io.EOFException
import java.io.IOException
import java.util.concurrent.CancellationException

class ErrorMappingTest {

    private interface Dummy {
        @GET("boom")
        suspend fun boom(): Unit
    }

    private lateinit var server: MockWebServer
    private lateinit var api: Dummy

    @Before
    fun setUp() {
        server = MockWebServer().apply { start() }
        api = Retrofit.Builder()
            .baseUrl(server.url("/").toString())
            .build()
            .create(Dummy::class.java)
    }

    @After
    fun tearDown() {
        server.close()
    }

    @Test
    fun `maps HttpException to NetworkError_Http on 404`() = runTest {
        server.enqueue(
            MockResponse(
                code = 404
            )
        )
        try {
            api.boom()
            fail("Expected HttpException")
        } catch (t: Throwable) {
            val mapped = t.toNetworkError()
            val http = mapped as NetworkError.Http
            assertEquals(404, http.code)
            assertNotNull(http.message)
        }
    }

    @Test
    fun `maps HttpException to NetworkError_Http on 500`() = runTest {
        server.enqueue(
            MockResponse(
                code = 500
            )
        )
        try {
            api.boom()
            fail("Expected HttpException")
        } catch (t: Throwable) {
            val mapped = t.toNetworkError()
            val http = mapped as NetworkError.Http
            assertEquals(500, http.code)
            assertNotNull(http.message)
        }
    }
}

class NetworkErrorMappingUnitTest {

    @Test
    fun `maps IOException to NetworkError_Io`() {
        val cause = IOException("io")
        val mapped = cause.toNetworkError()
        assertTrue(mapped is NetworkError.Io)
        val io = mapped as NetworkError.Io
        assertSame(cause, io.cause)
        assertEquals("io", io.cause.message)
    }

    @Test
    fun `maps EOFException subclass to NetworkError_Io`() {
        val cause = EOFException("eof")
        val mapped = cause.toNetworkError()
        assertTrue(mapped is NetworkError.Io)
        val io = mapped as NetworkError.Io
        assertSame(cause, io.cause)
    }

    @Test
    fun `maps IllegalStateException to NetworkError_Unknown`() {
        val cause = IllegalStateException("boom")
        val mapped = cause.toNetworkError()
        assertTrue(mapped is NetworkError.Unknown)
        val unknown = mapped as NetworkError.Unknown
        assertSame(cause, unknown.cause)
        assertEquals("boom", unknown.cause.message)
    }

    @Test
    fun `maps CancellationException to NetworkError_Unknown`() {
        val cause = CancellationException("job cancelled")
        val mapped = cause.toNetworkError()
        assertTrue(mapped is NetworkError.Unknown)
        val unknown = mapped as NetworkError.Unknown
        assertSame(cause, unknown.cause)
    }
}

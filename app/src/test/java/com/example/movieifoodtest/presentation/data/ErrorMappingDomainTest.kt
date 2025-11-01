package com.example.movieifoodtest.presentation.data

import com.example.movieifoodtest.data.mapper.toDomainException
import com.example.movieifoodtest.domain.model.DomainError
import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.http.GET
import java.io.EOFException
import java.io.IOException

class ErrorMappingDomainTest {

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
    fun `maps 401 to DomainError_Unauthorized`() = runTest {
        server.enqueue(
            MockResponse(
                code = 401
            )
        )
        try {
            api.boom()
            fail("Expected HttpException")
        } catch (t: Throwable) {
            val d = t.toDomainException().domain
            assertTrue(d is DomainError.Unauthorized)
        }
    }

    @Test
    fun `maps 404 to DomainError_NotFound`() = runTest {
        server.enqueue(
            MockResponse(
                code = 404
            )
        )
        try {
            api.boom(); fail("Expected HttpException")
        } catch (t: Throwable) {
            val d = t.toDomainException().domain
            assertTrue(d is DomainError.NotFound)
        }
    }

    @Test
    fun `maps 500 to DomainError_Http`() = runTest {
        server.enqueue(
            MockResponse(
                code = 500
            )
        )
        try {
            api.boom(); fail("Expected HttpException")
        } catch (t: Throwable) {
            val ex = t.toDomainException()
            val d = ex.domain as DomainError.Http
            assertEquals(500, d.code)
        }
    }

    @Test
    fun `maps IOException to DomainError_Network`() {
        val cause: Throwable = IOException("net")
        val ex = cause.toDomainException()
        assertTrue(ex.domain is DomainError.Network)
    }

    @Test
    fun `maps EOFException to DomainError_Network`() {
        val cause: Throwable = EOFException("eof")
        val ex = cause.toDomainException()
        assertTrue(ex.domain is DomainError.Network)
    }

    @Test
    fun `maps unknown Throwable to DomainError_Unknown`() {
        val cause: Throwable = IllegalStateException("boom")
        val ex = cause.toDomainException()
        val d = ex.domain as DomainError.Unknown
        assertEquals("boom", d.message)
    }
}

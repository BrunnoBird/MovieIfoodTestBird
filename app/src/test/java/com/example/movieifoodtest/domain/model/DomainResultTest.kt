package com.example.movieifoodtest.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class DomainResultTest {

    @Test
    fun `success result exposes value and flags`() {
        val result = DomainResult.success("value")

        assertTrue(result is DomainResult.Success)
        assertTrue(result.isSuccess)
        assertFalse(result.isFailure)
        assertEquals("value", result.getOrThrow())
        assertNull(result.exceptionOrNull())
    }

    @Test
    fun `failure result retains exception and flags`() {
        val exception = DomainException(DomainError.Network)
        val result = DomainResult.failure(exception)

        assertTrue(result is DomainResult.Failure)
        assertTrue(result.isFailure)
        assertFalse(result.isSuccess)
        assertSame(exception, result.exceptionOrNull())
        assertThrows(DomainException::class.java) {
            result.getOrThrow()
        }
    }

    @Test
    fun `failure factory wraps domain error in exception`() {
        val result = DomainResult.failure(DomainError.NotFound)

        assertTrue(result is DomainResult.Failure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertSame(DomainError.NotFound, exception?.domain)
    }

    @Test
    fun `map transforms success value`() {
        val result = DomainResult.success(2)

        val mapped = result.map { it * 3 }

        assertTrue(mapped is DomainResult.Success)
        assertEquals(6, mapped.getOrThrow())
    }

    @Test
    fun `map propagates failure`() {
        val exception = DomainException(DomainError.Unknown("boom"))
        val failure: DomainResult<Int> = DomainResult.failure(exception)
        var invoked = false

        val mapped = failure.map {
            invoked = true
            it * 2
        }

        assertFalse(invoked)
        assertTrue(mapped is DomainResult.Failure)
        assertSame(exception, mapped.exceptionOrNull())
    }

    @Test
    fun `fold runs success branch`() {
        val result = DomainResult.success(4)

        val folded = result.fold(
            onSuccess = { it * 2 },
            onFailure = { fail("Should not run failure branch") }
        )

        assertEquals(8, folded)
    }

    @Test
    fun `fold runs failure branch`() {
        val exception = DomainException(DomainError.Unauthorized)
        val result: DomainResult<String> = DomainResult.failure(exception)

        val folded = result.fold(
            onSuccess = { fail("Should not run success branch") },
            onFailure = { it }
        )

        assertSame(exception, folded)
    }
}
package com.example.movieifoodtest.core.network.http

import kotlinx.serialization.Serializable
import org.junit.Assert.assertEquals
import org.junit.Test

class JsonConfigTest {

    @Serializable data class Foo(val id: Int)

    @Test
    fun `ignores unknown keys when decoding`() {
        val json = createJson()
        val payload = """{"id":1,"extra":"ignored"}"""
        val obj = json.decodeFromString(Foo.serializer(), payload)
        assertEquals(1, obj.id)
    }
}

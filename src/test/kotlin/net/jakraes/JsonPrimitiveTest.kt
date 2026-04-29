package net.jakraes

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class JsonPrimitiveTest {

    @Test fun null_output()       = assertEquals("null",    JsonPrimitive(null).toString())
    @Test fun string_quoted()     = assertEquals("\"hi\"",  JsonPrimitive("hi").toString())
    @Test fun number_unquoted()   = assertEquals("42",      JsonPrimitive(42).toString())
    @Test fun boolean_unquoted()  = assertEquals("true",    JsonPrimitive(true).toString())
    @Test fun invalid_type_throws() { assertFailsWith<IllegalArgumentException> { JsonPrimitive(listOf(1)) } }

    @Test fun `escapes special characters`() =
        assertEquals("\"a\\\"b\\nc\"", JsonPrimitive("a\"b\nc").toString())
}

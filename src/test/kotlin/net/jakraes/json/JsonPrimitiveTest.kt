package net.jakraes.json

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class JsonPrimitiveTest {

    @Test fun `null serializes to null`()           = assertEquals("null",   JsonPrimitive(null).toString())
    @Test fun `string value is wrapped in quotes`() = assertEquals("\"hi\"", JsonPrimitive("hi").toString())
    @Test fun `number value is not quoted`()        = assertEquals("42",     JsonPrimitive(42).toString())
    @Test fun `boolean value is not quoted`()       = assertEquals("true",   JsonPrimitive(true).toString())
    @Test fun `unsupported type throws`()           { assertFailsWith<IllegalArgumentException> { JsonPrimitive(listOf(1)) } }

    @Test fun `special characters are escaped`() =
        assertEquals("\"a\\\"b\\nc\"", JsonPrimitive("a\"b\nc").toString())
}

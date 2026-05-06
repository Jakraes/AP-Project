package net.jakraes.json

import kotlin.test.Test
import kotlin.test.assertEquals

class JsonArrayTest {

    @Test fun `added element is retrievable by index`() {
        val arr = JsonArray().apply { add(JsonPrimitive("a")) }
        assertEquals("\"a\"", arr.get(0).toString())
    }

    @Test fun `removed element shifts remaining indices`() {
        val arr = JsonArray().apply { add(JsonPrimitive(1)); add(JsonPrimitive(2)); remove(0) }
        assertEquals(1, arr.size)
        assertEquals("2", arr.get(0).toString())
    }

    @Test fun `replaced element updates the stored value`() {
        val arr = JsonArray().apply { add(JsonPrimitive(1)); set(0, JsonPrimitive(99)) }
        assertEquals("99", arr.get(0).toString())
    }

    @Test fun `outputs comma separated values in brackets`() =
        assertEquals("[\"a\", null, \"b\"]", JsonArray().apply { add("a"); add(null); add("b") }.toString())

    @Test fun `empty array`() = assertEquals("[]", JsonArray().toString())
}

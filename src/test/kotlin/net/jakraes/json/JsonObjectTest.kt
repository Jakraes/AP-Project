package net.jakraes.json

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class JsonObjectTest {

    @Test fun `stored value is accessible by key`() {
        val obj = JsonObject().apply { set("x", JsonPrimitive(1)) }
        assertEquals("1", obj.get("x").toString())
    }

    @Test fun `primitive shorthand is stored correctly`() {
        val obj = JsonObject().apply { set("flag", true) }
        assertEquals("true", obj.get("flag").toString())
    }

    @Test fun `removed key is no longer present`() {
        val obj = JsonObject().apply { set("a", JsonPrimitive(1)); remove("a") }
        assertNull(obj.get("a"))
    }

    @Test fun `all inserted keys are returned`() {
        val obj = JsonObject().apply { set("a", JsonPrimitive(1)); set("b", JsonPrimitive(2)) }
        assertEquals(setOf("a", "b"), obj.keys())
    }

    @Test fun `empty is {}`() = assertEquals("{}", JsonObject().toString())

    @Test fun `type precedes properties`() {
        val out = JsonObject().apply { type = "Foo"; set("x", JsonPrimitive(1)) }.toString()
        assert(out.indexOf("\$type") < out.indexOf("\"x\""))
    }

    @Test fun `no id by default`() =
        assert(!JsonObject().apply { type = "T" }.toString().contains("\$id"))
}

package net.jakraes

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class JsonObjectTest {

    @Test fun `set and get`() {
        val obj = JsonObject().apply { set("x", JsonPrimitive(1)) }
        assertEquals("1", obj.get("x").toString())
    }

    @Test fun `raw value overload`() {
        val obj = JsonObject().apply { set("flag", true) }
        assertEquals("true", obj.get("flag").toString())
    }

    @Test fun remove() {
        val obj = JsonObject().apply { set("a", JsonPrimitive(1)); remove("a") }
        assertNull(obj.get("a"))
    }

    @Test fun keys() {
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

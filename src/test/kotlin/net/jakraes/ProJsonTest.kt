package net.jakraes

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

// ── domain fixtures ───────────────────────────────────────────────────────────

data class Point(val x: Int, val y: Int)
class Node(val label: String)
class Tree(val label: String, @Reference val children: List<Tree>)
class Renamed(@JsonProperty("desc") val description: String, @JsonIgnore val secret: String)

@JsonString(DateSerializer::class)
data class SimpleDate(val day: Int, val month: Int, val year: Int)
class DateSerializer : JsonStringSerializer<SimpleDate> {
    override fun serialize(value: SimpleDate) = "%02d/%02d/%04d".format(value.day, value.month, value.year)
}

// ── tests ─────────────────────────────────────────────────────────────────────

class ProJsonTest {

    private val proJson = ProJson()

    @Test fun `null`() = assertEquals("null", proJson.toJson(null).toString())

    @Test fun primitives() {
        assertEquals("\"hi\"", proJson.toJson("hi").toString())
        assertEquals("3.14",   proJson.toJson(3.14).toString())
        assertEquals("false",  proJson.toJson(false).toString())
    }

    @Test fun `map has no type`() {
        val obj = assertIs<JsonObject>(proJson.toJson(mapOf("k" to 1)))
        assertTrue(!obj.toString().contains("\$type"))
    }

    @Test fun `iterable to array`() =
        assertEquals(3, assertIs<JsonArray>(proJson.toJson(listOf(1, 2, 3))).size)

    @Test fun `data class has no id, regular class does`() {
        val dataJson  = proJson.toJson(Point(1, 2)) as JsonObject
        val classJson = proJson.toJson(Node("x")) as JsonObject
        assertNull(dataJson.id)
        assertNotNull(classJson.id)
    }

    @Test fun `same instance same id`() {
        val node = Node("a")
        val id1 = (proJson.toJson(node) as JsonObject).id
        val id2 = (proJson.toJson(node) as JsonObject).id
        assertEquals(id1, id2)
    }

    @Test fun JsonIgnore() =
        assertTrue(!proJson.toJson(Renamed("v", "hidden")).toString().contains("secret"))

    @Test fun JsonProperty() =
        assertTrue(proJson.toJson(Renamed("v", "h")).toString().contains("desc"))

    @Test fun Reference() {
        val child = Tree("child", emptyList())
        val arr = assertIs<JsonArray>(proJson.toJson(listOf(child, Tree("root", listOf(child)))))
        assertTrue(arr.get(1).toString().contains("\$ref"))
    }

    @Test fun JsonString() =
        assertEquals("\"01/06/2026\"", proJson.toJson(SimpleDate(1, 6, 2026)).toString())
}

package net.jakraes

import java.util.UUID

/** A JSON object (key-value pairs). Property order is preserved. */
class JsonObject : JsonValue() {
    internal var id: UUID? = null
    internal var type: String? = null

    private val members: LinkedHashMap<String, JsonValue> = LinkedHashMap()

    /** Returns the value for [key], or null if absent. */
    fun get(key: String): JsonValue? = members[key]

    /** Sets [key] to a [JsonValue]. */
    fun set(key: String, value: JsonValue) { members[key] = value }

    /** Sets [key] to a raw primitive value, wrapped in [JsonPrimitive]. */
    fun set(key: String, value: Any?) = set(key, JsonPrimitive(value))

    /** Removes [key]. No-op if absent. */
    fun remove(key: String) { members.remove(key) }

    /** Returns the set of property keys. */
    fun keys(): Set<String> = members.keys

    /** Returns all key-value entries. */
    fun entries(): Set<Map.Entry<String, JsonValue>> = members.entries

    override fun toString(): String {
        val entries = buildList {
            if (id != null) add("\"\$id\": \"$id\"")
            if (type != null) add("\"\$type\": \"$type\"")
            members.forEach { (k, v) -> add("\"$k\": $v") }
        }
        if (entries.isEmpty()) return "{}"
        return "{\n${entries.joinToString(",\n") { "\t$it" }}\n}"
    }
}

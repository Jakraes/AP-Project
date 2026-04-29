package net.jakraes

import java.util.UUID

/** A JSON object (ordered key-value pairs). [id] and [type] are managed by [ProJson]. */
class JsonObject : JsonValue() {
    internal var id: UUID? = null
    internal var type: String? = null

    private val members = LinkedHashMap<String, JsonValue>()

    /** Returns the value for [key], or null if absent. */
    fun get(key: String): JsonValue? = members[key]

    fun set(key: String, value: JsonValue) { members[key] = value }

    /** Wraps [value] in a [JsonPrimitive] and sets it. */
    fun set(key: String, value: Any?) = set(key, JsonPrimitive(value))

    fun remove(key: String) { members.remove(key) }

    fun keys(): Set<String> = members.keys

    fun entries(): Set<Map.Entry<String, JsonValue>> = members.entries

    override fun toString(): String {
        val lines = buildList {
            if (id != null)   add("\"\$id\": \"$id\"")
            if (type != null) add("\"\$type\": \"$type\"")
            members.forEach { (k, v) -> add("\"$k\": $v") }
        }
        if (lines.isEmpty()) return "{}"
        return "{\n${lines.joinToString(",\n") { "\t$it" }}\n}"
    }
}

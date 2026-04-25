package net.jakraes

import java.util.UUID

class JsonObject : JsonValue() {
    internal var id: UUID? = null
    internal var type: String? = null

    private val members: LinkedHashMap<String, JsonValue> = LinkedHashMap()

    fun get(key: String): JsonValue? = members[key]

    fun set(key: String, value: JsonValue) {
        members[key] = value
    }

    // overload for easy setting :)
    fun set(key: String, value: Any?) {
        members[key] = JsonPrimitive(value)
    }

    fun remove(key: String) {
        members.remove(key)
    }

    fun keys(): Set<String> = members.keys

    fun entries(): Set<Map.Entry<String, JsonValue>> = members.entries

    override fun toString(): String {
        val entries = buildList {
            if (id != null) add("\"\$id\": \"$id\"")
            if (type != null) add("\"\$type\": \"$type\"")
            members.forEach { (k, v) -> add("\"$k\": $v") }
        }

        return "{\n${entries.joinToString(",\n") { "\t$it" }}\n}"
    }
}
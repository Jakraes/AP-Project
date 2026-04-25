package net.jakraes

class JsonPrimitive(val value: Any?) : JsonValue() {
    init { require(value == null || value is String || value is Number || value is Boolean) }

    override fun toString(): String =
        if (value is String) "\"$value\""
        else value.toString()
}
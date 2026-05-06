package net.jakraes.json

/** Holds a single JSON value: [String], [Number], [Boolean], or null. Any other type throws. */
class JsonPrimitive(val value: Any?) : JsonValue() {
    init { require(value == null || value is String || value is Number || value is Boolean) }

    override fun toString(): String =
        if (value is String) "\"${value.escape()}\""
        else value?.toString() ?: "null"

    private fun String.escape() = replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
}

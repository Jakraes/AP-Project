package net.jakraes.json

/** Base type for all JSON nodes. */
sealed class JsonValue {
    abstract override fun toString(): String
}

/** Visits this node, then recurses into children depth-first. */
fun JsonValue.accept(visitor: (JsonValue) -> Unit) {
    visitor(this)
    when (this) {
        is JsonObject                -> entries().forEach { (_, v) -> v.accept(visitor) }
        is JsonArray                 -> iterator().forEach { it.accept(visitor) }
        is JsonPrimitive, is JsonReference -> {}
    }
}

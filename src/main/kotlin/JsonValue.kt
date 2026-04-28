package net.jakraes

/** Base type for all JSON nodes. */
sealed class JsonValue {
    abstract override fun toString(): String
}

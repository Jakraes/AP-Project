package net.jakraes

/** A JSON array. */
class JsonArray : JsonValue() {
    private val members: MutableList<JsonValue> = mutableListOf()

    /** Returns the element at [index]. Throws if out of bounds. */
    fun get(index: Int): JsonValue = members[index]

    /** Replaces the element at [index] with [value]. */
    fun set(index: Int, value: JsonValue) { members[index] = value }

    /** Appends a [JsonValue]. */
    fun add(value: JsonValue) { members.add(value) }

    /** Appends a raw primitive value, wrapped in [JsonPrimitive]. */
    fun add(value: Any?) = add(JsonPrimitive(value))

    /** Appends all elements from [values]. */
    fun addAll(values: Iterable<JsonValue>) { values.forEach { add(it) } }

    /** Removes the element at [index]. */
    fun remove(index: Int) { members.removeAt(index) }

    /** Number of elements. */
    val size: Int get() = members.size

    /** Returns an iterator over the elements. */
    fun iterator(): Iterator<JsonValue> = members.iterator()

    override fun toString(): String = "[${members.joinToString(", ")}]"
}

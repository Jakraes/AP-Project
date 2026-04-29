package net.jakraes

/** A JSON array. */
class JsonArray : JsonValue() {
    private val members: MutableList<JsonValue> = mutableListOf()

    fun get(index: Int): JsonValue = members[index]
    fun set(index: Int, value: JsonValue) { members[index] = value }
    fun add(value: JsonValue) { members.add(value) }
    fun add(value: Any?) = add(JsonPrimitive(value))
    fun addAll(values: Iterable<JsonValue>) { members.addAll(values) }
    fun remove(index: Int) { members.removeAt(index) }

    val size: Int get() = members.size
    fun iterator(): Iterator<JsonValue> = members.iterator()

    override fun toString(): String = "[${members.joinToString(", ")}]"
}

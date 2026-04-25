package net.jakraes

class JsonArray: JsonValue() {
    private val members: MutableList<JsonValue> = mutableListOf()

    fun get(index: Int): JsonValue = members[index]

    fun set(index: Int, value: JsonValue) { members[index] = value }

    fun add(value: JsonValue) = members.add(value)

    fun addAll(values: Iterable<JsonValue>) = members.addAll(values)

    fun remove(index: Int) = members.removeAt(index)

    val size: Int get() = members.size

    fun iterator(): Iterator<JsonValue> = members.iterator()

    override fun toString(): String {
        return super.toString()
    }
}
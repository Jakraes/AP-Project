package net.jakraes

import java.util.IdentityHashMap
import java.util.UUID
import kotlin.reflect.full.memberProperties

class ProJson {
    private val map: HashMap<Any, UUID> = HashMap()

    fun toJson(obj: Any?): JsonValue = when (obj) {
        null           -> JsonPrimitive(null)
        is Boolean     -> JsonPrimitive(obj)
        is Number      -> JsonPrimitive(obj)
        is String      -> JsonPrimitive(obj)
        is Map<*, *>   -> mapToJsonObject(obj)
        is Iterable<*> -> iterableToJsonArray(obj)
        else           -> reflectToJsonObject(obj)
    }

    private fun mapToJsonObject(map: Map<*, *>): JsonObject {
        val result = JsonObject()

        for ((k, v) in map) result.set(k.toString(), toJson(v))

        return result
    }

    private fun iterableToJsonArray(col: Iterable<*>): JsonArray {
        val result = JsonArray()

        for (item in col) result.add(toJson(item))

        return result
    }

    private fun reflectToJsonObject(obj: Any): JsonObject {
        val klass = obj::class

        val result = JsonObject()

        result.type = klass.simpleName

        if (!klass.isData) {
            result.id = map.getOrPut(obj) { UUID.randomUUID() }
        }

        for (prop in klass.memberProperties) {
            result.set(prop.name, toJson(prop.getter.call(obj)))
        }

        return result
    }
}

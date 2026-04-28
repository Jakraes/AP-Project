package net.jakraes

import java.util.UUID
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

/**
 * Entry point for JSON serialization.
 *
 * One instance = one serialization context. Reuse the same instance when serializing
 * a graph so that cross-object `$ref` links are resolved correctly.
 */
class ProJson {
    private val uuids: HashMap<Any, UUID> = HashMap()

    /**
     * Converts [obj] to a [JsonValue].
     *
     * - `null`, primitives, strings -> [JsonPrimitive]
     * - [Map] -> [JsonObject] (no `$type`)
     * - [Iterable] -> [JsonArray]
     * - Any other object -> [JsonObject] via reflection, respecting [@JsonIgnore],
     *   [@JsonProperty], [@Reference], and [@JsonString].
     */
    fun toJson(obj: Any?): JsonValue {
        if (obj != null) {
            obj::class.findAnnotation<JsonString>()?.let { ann ->
                @Suppress("UNCHECKED_CAST")
                val serializer = ann.serializer.createInstance() as JsonStringSerializer<Any>
                return JsonPrimitive(serializer.serialize(obj))
            }
        }
        return when (obj) {
            null           -> JsonPrimitive(null)
            is Boolean     -> JsonPrimitive(obj)
            is Number      -> JsonPrimitive(obj)
            is String      -> JsonPrimitive(obj)
            is Map<*, *>   -> mapToJsonObject(obj)
            is Iterable<*> -> iterableToJsonArray(obj)
            else           -> reflectToJsonObject(obj)
        }
    }

    /** Returns a `$ref` stub if [obj] was already serialized, otherwise serializes it. */
    private fun resolveOrRef(obj: Any): JsonValue {
        val existing = uuids[obj]
        return if (existing != null) JsonReference(existing) else toJson(obj)
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
        return JsonObject().apply {
            type = klass.simpleName
            if (!klass.isData) id = uuids.getOrPut(obj) { UUID.randomUUID() }

            klass.memberProperties
                .filterNot { it.hasAnnotation<JsonIgnore>() }
                .forEach { prop ->
                    val key = prop.findAnnotation<JsonProperty>()?.name ?: prop.name
                    val value = prop.getter.call(obj)
                    set(key, if (prop.hasAnnotation<Reference>()) resolveRef(value) else toJson(value))
                }
        }
    }

    /** Handles a `@Reference`-annotated property value: resolves single objects or iterables. */
    private fun resolveRef(value: Any?): JsonValue = when (value) {
        null           -> JsonPrimitive(null)
        is Iterable<*> -> JsonArray().apply { value.forEach { add(if (it != null) resolveOrRef(it) else JsonPrimitive(null)) } }
        else           -> resolveOrRef(value)
    }
}

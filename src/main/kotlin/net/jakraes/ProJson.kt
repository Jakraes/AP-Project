package net.jakraes

import net.jakraes.json.*
import java.util.UUID
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

/**
 * Serializes Kotlin objects into [JsonValue] trees.
 *
 * One instance = one serialization context. Reuse across a full object graph
 * so that [JsonReference] ($ref) links resolve correctly across objects.
 */
class ProJson {
    private val uuids = HashMap<Any, UUID>()

    /**
     * Converts [obj] to a [JsonValue]:
     * - null / Boolean / Number / String → [JsonPrimitive]
     * - [@JsonString][JsonString] class  → [JsonPrimitive] via custom serializer
     * - [Map]        → [JsonObject] (no $type)
     * - [Iterable]   → [JsonArray]
     * - [JsonValue]  → returned as-is
     * - anything else → [JsonObject] via reflection
     */
    fun toJson(obj: Any?): JsonValue = when {
        obj == null      -> JsonPrimitive(null)
        obj is JsonValue -> obj
        obj is Boolean   -> JsonPrimitive(obj)
        obj is Number    -> JsonPrimitive(obj)
        obj is String    -> JsonPrimitive(obj)
        else -> obj::class.findAnnotation<JsonString>()
            ?.let { serializeAsString(obj, it) }
            ?: when (obj) {
                is Map<*, *>   -> mapToJsonObject(obj)
                is Iterable<*> -> iterableToJsonArray(obj)
                else           -> reflectToJsonObject(obj)
            }
    }

    @Suppress("UNCHECKED_CAST")
    private fun serializeAsString(obj: Any, ann: JsonString) =
        JsonPrimitive((ann.serializer.createInstance() as JsonStringSerializer<Any>).serialize(obj))

    private fun mapToJsonObject(map: Map<*, *>) = JsonObject().apply {
        map.forEach { (k, v) -> set(k.toString(), toJson(v)) }
    }

    private fun iterableToJsonArray(col: Iterable<*>) = JsonArray().apply {
        col.forEach { add(toJson(it)) }
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

    // Returns a $ref if obj was already serialized in this context, otherwise serializes it.
    private fun resolveOrRef(obj: Any): JsonValue =
        uuids[obj]?.let { JsonReference(it) } ?: toJson(obj)

    private fun resolveRef(value: Any?): JsonValue = when (value) {
        null           -> JsonPrimitive(null)
        is Iterable<*> -> JsonArray().apply {
            value.forEach { add(it?.let { resolveOrRef(it) } ?: JsonPrimitive(null)) }
        }
        else           -> resolveOrRef(value)
    }
}

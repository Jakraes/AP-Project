package net.jakraes

import java.util.UUID
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

/**
 * Converts any Kotlin object into a [JsonValue] tree.
 *
 * One instance = one serialization context. Reuse the same instance across a full
 * object graph so that [$ref][JsonReference] links resolve correctly.
 */
class ProJson {
    // Tracks which objects have already been assigned a UUID in this context.
    private val uuids: HashMap<Any, UUID> = HashMap()

    /**
     * Converts [obj] to a [JsonValue]:
     * - null / Boolean / Number / String  →  [JsonPrimitive]
     * - [Map]       →  [JsonObject] (no $type)
     * - [Iterable]  →  [JsonArray]
     * - anything else  →  [JsonObject] via reflection
     *
     * If the class is annotated with [@JsonString][JsonString], it is serialized
     * as a string instead.
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
            is JsonValue   -> obj
            is Boolean     -> JsonPrimitive(obj)
            is Number      -> JsonPrimitive(obj)
            is String      -> JsonPrimitive(obj)
            is Map<*, *>   -> mapToJsonObject(obj)
            is Iterable<*> -> iterableToJsonArray(obj)
            else           -> reflectToJsonObject(obj)
        }
    }

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

    // If obj was already serialized in this context, emit a $ref instead of duplicating it.
    private fun resolveOrRef(obj: Any): JsonValue =
        uuids[obj]?.let { JsonReference(it) } ?: toJson(obj)

    private fun resolveRef(value: Any?): JsonValue = when (value) {
        null           -> JsonPrimitive(null)
        is Iterable<*> -> JsonArray().apply {
            value.forEach { item ->
                add(item?.let { resolveOrRef(it) } ?: JsonPrimitive(null))
            }
        }
        else           -> resolveOrRef(value)
    }
}

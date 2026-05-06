package net.jakraes

import kotlin.reflect.KClass

/** Serializes this property as a `$ref` stub instead of an inline object. */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Reference

/** Overrides the JSON key name for this property. */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonProperty(val name: String)

/** Excludes this property from JSON output. */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonIgnore

/**
 * Serializes this class as a plain JSON string using [serializer].
 * The serializer must have a no-arg constructor.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonString(val serializer: KClass<out JsonStringSerializer<*>>)

/** Converts an object to its string representation for use with [@JsonString]. */
interface JsonStringSerializer<T : Any> {
    fun serialize(value: T): String
}

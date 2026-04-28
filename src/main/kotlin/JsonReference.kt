package net.jakraes

import java.util.UUID

/** A JSON reference stub emitted as `{ "$ref": "<uuid>" }`. Managed internally by [ProJson]. */
internal class JsonReference(val targetId: UUID) : JsonValue() {
    override fun toString(): String = "{ \"\$ref\": \"$targetId\" }"
}

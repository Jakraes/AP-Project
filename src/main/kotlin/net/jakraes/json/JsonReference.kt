package net.jakraes.json

import java.util.UUID

/** A JSON reference stub emitted as `{ "$ref": "<uuid>" }`. Managed internally by [net.jakraes.ProJson]. */
internal class JsonReference(val targetId: UUID) : JsonValue() {
    override fun toString(): String = "{ \"\$ref\": \"$targetId\" }"
}

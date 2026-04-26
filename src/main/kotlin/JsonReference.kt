package net.jakraes

import java.util.UUID

internal class JsonReference(val targetId: UUID) : JsonValue() {
    override fun toString(): String = "{ \"\$ref\": \"$targetId\" }"
}

package net.jakraes

fun main() {
    val obj = JsonObject().apply {
        type = "Object"
        set("count", JsonPrimitive(2))
    }

    val nested = JsonObject().apply {
        set("greeting", "Hello")
        set("active", true)
    }

    obj.set("nested", nested)

    println(obj)
}
package net.jakraes

fun main() {
    val obj = JsonObject().apply {
        type = "Yippie"
        set("what", 2)
    }

    val nested = JsonObject().apply {
        set("hello", "world!")
        set("yup", true)
    }

    obj.set("nested", nested)

    println(obj)
}
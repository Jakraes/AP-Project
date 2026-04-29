package net.jakraes

class Test(
    val test_one: Int,
    val test_two: String,
    @JsonIgnore val test_three: String
) {
}

class What(
    val what_one: String,
    @Reference val what_ref: List<Test>
) {
}

fun main() {
    val json = ProJson()

    val test = Test(12, "Hello", "World!")
    val what = What("What?", listOf(test))

    val arr = JsonArray().apply { add(json.toJson(test)); add(json.toJson(what)) }

    println(arr)
}
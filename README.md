# ProJson

Kotlin library that converts objects to JSON. Handles object graph references via UUIDs transparently.

Download the latest `*-all.jar` from [Releases](../../releases).

---

## toJson

```kotlin
val json = ProJson().toJson(Date(31, 4, 2026)) as JsonObject
json.set("year", 2027)
println(json)
```
```json
{
    "$type": "Date",
    "day": 31,
    "month": 4,
    "year": 2027
}
```

| Input | Output |
|---|---|
| `null`, `String`, `Number`, `Boolean` | `JsonPrimitive` |
| `Map` | `JsonObject` (no `$type`) |
| `Iterable` | `JsonArray` |
| `data class` | `JsonObject` with `$type`, no `$id` |
| any other class | `JsonObject` with `$type` and `$id` |

---

## JsonObject

```kotlin
val obj = JsonObject()
obj.set("name", "Alice")        // raw value
obj.set("age", JsonPrimitive(30))
obj.get("name")                 // JsonPrimitive("Alice")
obj.remove("name")
obj.keys()                      // Set<String>
obj.entries()                   // Set<Map.Entry<String, JsonValue>>
```

## JsonArray

```kotlin
val arr = JsonArray()
arr.add("x")
arr.add(JsonPrimitive(42))
arr.set(0, JsonPrimitive("y"))
arr.remove(0)
arr.size
```

---

## References

Use `@Reference` on properties that point to shared objects. The first occurrence is serialized in full with a `$id`; subsequent ones become `{ "$ref": "..." }`.

```kotlin
class Task(
    val description: String,
    val deadline: Date?,
    @Reference val dependencies: List<Task>
)

val t1 = Task("T1", Date(30, 2, 2026), emptyList())
val t2 = Task("T2", Date(31, 4, 2026), emptyList())
val t3 = Task("T3", null, listOf(t1, t2))

// Use the same ProJson instance for the whole graph
val json = ProJson().toJson(listOf(t1, t2, t3))
```

---

## Annotations

**`@JsonProperty("key")`** — override the JSON key name.

**`@JsonIgnore`** — exclude a property from output.

```kotlin
class Task(
    @JsonProperty("desc") val description: String,
    @JsonIgnore val internalId: Int
)
// { "$type": "Task", "desc": "..." }
```

**`@JsonString(Serializer::class)`** — serialize the whole object as a string. The serializer needs a no-arg constructor.

```kotlin
class DateSerializer : JsonStringSerializer<Date> {
    override fun serialize(value: Date) = "${value.day}/${value.month}/${value.year}"
}

@JsonString(DateSerializer::class)
data class Date(val day: Int, val month: Int, val year: Int)

ProJson().toJson(Date(1, 6, 2026)) // "1/6/2026"
```

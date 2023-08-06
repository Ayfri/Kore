package utils

import kotlinx.serialization.json.*

fun JsonObject.copyExcept(vararg keys: String) = buildJsonObject {
	forEach { (key, value) -> if (key !in keys) put(key, value) }
}

operator fun JsonObject.set(key: String, value: JsonObject) = buildJsonObject {
	put(key, value)
	forEach(::put)
}

fun JsonObject.setType(type: String, typePropertyName: String = "type") = buildJsonObject {
	put(typePropertyName, type)
	forEach { (key, value) -> if (key != typePropertyName) put(key, value) }
}

fun JsonObject.getPropertyContent(propertyName: String) = get(propertyName)!!.jsonPrimitive.content

fun JsonObjectBuilder.copyAllFrom(other: JsonObject, vararg exceptKeys: String) = apply {
	other.forEach { (key, value) -> if (key !in exceptKeys) put(key, value) }
}

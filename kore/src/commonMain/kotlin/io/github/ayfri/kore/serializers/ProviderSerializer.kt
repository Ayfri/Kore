package io.github.ayfri.kore.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/** Serializes a worldgen provider as its bare value when its type is `minecraft:`[constantName], as-is otherwise. */
open class ProviderSerializer<T : Any>(
	kSerializer: KSerializer<T>,
	private val constantName: String = "constant",
	private val typePropertyName: String = "type",
	private val valuePropertyName: String = "value",
) : JsonTransformingSerializer<T>(kSerializer) {
	override fun transformSerialize(element: JsonElement): JsonElement {
		val provider = element.jsonObject
		val isConstant = provider.getValue(typePropertyName).jsonPrimitive.content == "minecraft:$constantName"
		return if (isConstant) provider.getValue(valuePropertyName) else provider
	}
}

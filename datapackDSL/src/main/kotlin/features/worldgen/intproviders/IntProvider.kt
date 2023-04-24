package features.worldgen.intproviders

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

typealias IntProvider = @Serializable(IntProviderSurrogate.Companion.IntProviderSerializer::class) IntProviderSurrogate

@Serializable
sealed interface IntProviderSurrogate {
	companion object {
		object IntProviderSerializer : JsonTransformingSerializer<IntProvider>(serializer()) {
			override fun transformSerialize(element: JsonElement) = when (val type = element.jsonObject["type"]!!.jsonPrimitive.content) {
				"minecraft:constant" -> JsonPrimitive(element.jsonObject["value"]!!.jsonPrimitive.int)
				else -> buildJsonObject {
					put("type", type)
					putJsonObject("value") {
						element.jsonObject.forEach { (key, value) ->
							if (key == "type") return@forEach
							put(key, value)
						}
					}
				}
			}
		}
	}
}

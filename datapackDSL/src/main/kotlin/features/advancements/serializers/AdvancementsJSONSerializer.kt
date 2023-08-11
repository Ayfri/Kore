package features.advancements.serializers

import arguments.selector.SelectorAdvancements
import kotlinx.serialization.json.*

object AdvancementsJSONSerializer : JsonTransformingSerializer<SelectorAdvancements>(SelectorAdvancements.serializer()) {
	override fun transformSerialize(element: JsonElement): JsonElement {
		return buildJsonObject {
			element.jsonObject["advancements"]!!.jsonArray.forEach { value ->
				val criteria = value.jsonObject["criteria"]!!.jsonObject
				val name = value.jsonObject["advancement"]!!.jsonPrimitive.content
				val jsonObject = when {
					criteria.isNotEmpty() -> buildJsonObject {
						criteria.jsonObject.forEach { (key, value) -> put(key, value) }
					}

					else -> JsonPrimitive(value.jsonObject["done"]!!.jsonPrimitive.boolean)
				}

				put(name, jsonObject)
			}
		}
	}
}

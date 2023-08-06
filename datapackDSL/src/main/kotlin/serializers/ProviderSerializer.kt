package serializers

import utils.copyAllFrom
import utils.getPropertyContent
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.*

@OptIn(InternalSerializationApi::class)
open class ProviderSerializer<T : Any>(
	private val kSerializer: KSerializer<T>,
	private val constantName: String = "constant",
	private val weightedListName: String = "weighted_list",
	private val typePropertyName: String = "type",
	private val valuePropertyName: String = "value",
) : JsonTransformingSerializer<T>(kSerializer) {
	override fun transformSerialize(element: JsonElement) = when (val type = element.jsonObject.getPropertyContent(typePropertyName)) {
		"minecraft:$constantName" -> element.jsonObject[valuePropertyName]!!
		"minecraft:$weightedListName" -> element.jsonObject
		else -> buildJsonObject {
			put(typePropertyName, type)
			putJsonObject(valuePropertyName) {
				copyAllFrom(element.jsonObject, typePropertyName)
			}
		}
	}
}

package features.tags

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.jsonObject

@Serializable
data class TagEntry(
	@SerialName("id") val name: String,
	val required: Boolean? = null,
) {
	companion object {
		object TagEntrySerializer : JsonTransformingSerializer<TagEntry>(serializer()) {
			override fun transformSerialize(element: JsonElement) = when {
				element.jsonObject["required"] == null -> element.jsonObject["id"]!!
				else -> element
			}
		}
	}
}

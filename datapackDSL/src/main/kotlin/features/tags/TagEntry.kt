package features.tags

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

@Serializable(with = TagEntry.Companion.TagEntrySerializer::class)
data class TagEntry(
	@SerialName("id")
	val name: String,
	val required: Boolean? = null,
) {
	companion object {
		object TagEntrySerializer : KSerializer<TagEntry> {
			override val descriptor = buildClassSerialDescriptor("TagEntry")

			override fun deserialize(decoder: Decoder) = error("TagEntry cannot be deserialized")

			override fun serialize(encoder: Encoder, value: TagEntry) {
				require(encoder is JsonEncoder) { "TagEntry can only be serialized as Json" }

				val result = when (value.required) {
					null -> JsonPrimitive(value.name)
					else -> buildJsonObject {
						put("id", JsonPrimitive(value.name))
						put("required", JsonPrimitive(value.required))
					}
				}

				encoder.encodeJsonElement(result)
			}
		}
	}
}

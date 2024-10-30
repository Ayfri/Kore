package io.github.ayfri.kore.features.tags

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.json.JsonDecoder
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
			override val descriptor = buildClassSerialDescriptor("TagEntry") {
				element<String>("id")
				element<Boolean>("required")
			}

			override fun deserialize(decoder: Decoder): TagEntry {
				require(decoder is JsonDecoder) { "TagEntry can only be deserialized from Json" }

				return when (val element = decoder.decodeJsonElement()) {
					is JsonPrimitive -> TagEntry(element.content)
					else -> decoder.decodeStructure(descriptor) {
						var id: String? = null
						var required: Boolean? = null

						loop@ while (true) {
							when (val index = decodeElementIndex(descriptor)) {
								0 -> id = decodeStringElement(descriptor, index)
								1 -> required = decodeBooleanElement(descriptor, index)
								CompositeDecoder.DECODE_DONE -> break@loop
								else -> error("Unexpected index: $index")
							}
						}

						TagEntry(id!!, required)
					}
				}
			}

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

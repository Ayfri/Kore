package data.block

import arguments.types.resources.BlockArgument
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement

@Serializable(with = BlockState.Companion.BlockStateSerializer::class)
data class BlockState(
	@SerialName("Name")
	val name: BlockArgument,
	@SerialName("Properties")
	val properties: Map<String, String>? = null,
) {
	companion object {
		object BlockStateSerializer : KSerializer<BlockState> {
			override val descriptor = buildClassSerialDescriptor("BlockState") {
				element<String>("Name")
				element<Map<String, String>>("Properties")
			}

			override fun deserialize(decoder: Decoder) = error("BlockState cannot be deserialized")
			override fun serialize(encoder: Encoder, value: BlockState) =
				when (encoder) {
					is JsonEncoder -> {
						val json = buildJsonObject {
							put("Name", encoder.json.encodeToJsonElement(value.name))

							if (value.properties?.isNotEmpty() != true) return@buildJsonObject
							put("Properties", encoder.json.encodeToJsonElement(value.properties))
						}

						encoder.encodeJsonElement(json)
					}

					else -> encoder.encodeStructure(descriptor) {
						encodeStringElement(descriptor, 0, value.name.asString())
						if (value.properties.isNullOrEmpty()) return@encodeStructure
						encodeSerializableElement(descriptor, 1, MapSerializer(String.serializer(), String.serializer()), value.properties)
					}
				}
		}
	}
}

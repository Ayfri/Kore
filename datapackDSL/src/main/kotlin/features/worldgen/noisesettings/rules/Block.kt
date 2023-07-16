package features.worldgen.noisesettings.rules

import arguments.types.resources.BlockArgument
import data.block.BlockState
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement

@Serializable
data class Block(
	@Serializable(with = BlockStateSerializer::class)
	var resultState: BlockState? = null,
) : SurfaceRule() {
	companion object {
		object BlockStateSerializer : KSerializer<BlockState> {
			override val descriptor = buildClassSerialDescriptor("BlockState") {
				element<String>("Name")
				element<Map<String, String>>("Properties")
			}

			override fun deserialize(decoder: Decoder) = error("BlockState cannot be deserialized")
			override fun serialize(encoder: Encoder, value: BlockState) {
				require(encoder is JsonEncoder) { "BlockState can only be serialized to Json" }

				val json = buildJsonObject {
					put("Name", encoder.json.encodeToJsonElement(value.name))

					if (value.properties.isNullOrEmpty()) return@buildJsonObject
					put("Properties", encoder.json.encodeToJsonElement(value.properties))
				}

				encoder.encodeJsonElement(json)
			}
		}
	}
}

fun block(name: BlockArgument, block: MutableMap<String, String>.() -> Unit = {}) = Block(BlockState(name, buildMap(block)))
fun block(name: BlockArgument, properties: Map<String, String>) = Block(BlockState(name, properties))

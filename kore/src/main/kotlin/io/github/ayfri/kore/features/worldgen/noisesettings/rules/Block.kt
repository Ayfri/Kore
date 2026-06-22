package io.github.ayfri.kore.features.worldgen.noisesettings.rules

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.serializers.decodeJsonObject
import io.github.ayfri.kore.serializers.splitNamespacedId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

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

			override fun deserialize(decoder: Decoder): BlockState {
				val json = decoder.decodeJsonObject()
				val (name, namespace) = json.getValue("Name").jsonPrimitive.content.splitNamespacedId()
				val properties = json["Properties"]?.jsonObject?.mapValues { it.value.jsonPrimitive.content }
				return BlockState(BlockArgument(name, namespace), properties)
			}
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

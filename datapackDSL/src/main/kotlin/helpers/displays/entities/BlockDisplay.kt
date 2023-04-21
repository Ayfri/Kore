package helpers.displays.entities

import data.block.BlockState
import generated.Entities
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure

@Serializable(with = BlockDisplay.Companion.DisplayEntitySerializer::class)
data class BlockDisplay(
	var blockState: BlockState? = null,
) : DisplayEntity() {
	override val entityType = Entities.BLOCK_DISPLAY

	override fun toString() =
		"BlockDisplay(blockState='$blockState', billboardMode=$billboardMode, brightness=$brightness, glowColorOverride=$glowColorOverride, height=$height, interpolationDuration=$interpolationDuration, startInterpolation=$startInterpolation, shadowRadius=$shadowRadius, shadowStrength=$shadowStrength, transformation=$transformation, viewRange=$viewRange, width=$width)"

	companion object {
		object DisplayEntitySerializer : KSerializer<BlockDisplay> {
			override val descriptor = buildClassSerialDescriptor("BlockDisplay") {
				element("block_state", BlockState.serializer().descriptor)
				addDisplayEntity()
			}

			override fun deserialize(decoder: Decoder) = error("Not implemented")

			override fun serialize(encoder: Encoder, value: BlockDisplay) {
				encoder.encodeStructure(descriptor) {
					value.blockState?.let { encodeSerializableElement(descriptor, 0, BlockState.serializer(), it) }
					encodeDisplayEntity(value)
				}
			}
		}
	}
}

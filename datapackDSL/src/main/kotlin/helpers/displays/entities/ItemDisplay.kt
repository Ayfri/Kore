package helpers.displays.entities

import data.item.ItemStack
import generated.Entities
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure

@Serializable(with = ItemDisplay.Companion.DisplayEntitySerializer::class)
data class ItemDisplay(
	var item: ItemStack? = null,
	@SerialName("item_display")
	var displayMode: ItemDisplayModelMode? = null,
) : DisplayEntity() {
	override val entityType = Entities.ITEM_DISPLAY

	override fun toString() =
		"ItemDisplay(item=$item, displayMode=$displayMode, billboardMode=$billboardMode, brightness=$brightness, glowColorOverride=$glowColorOverride, height=$height, interpolationDuration=$interpolationDuration, startInterpolation=$startInterpolation, shadowRadius=$shadowRadius, shadowStrength=$shadowStrength, transformation=$transformation, viewRange=$viewRange, width=$width)"

	companion object {
		object DisplayEntitySerializer : KSerializer<ItemDisplay> {
			override val descriptor = buildClassSerialDescriptor("ItemDisplay") {
				element("item", ItemStack.serializer().descriptor)
				element("item_display", ItemDisplayModelMode.serializer().descriptor)
				addDisplayEntity()
			}

			override fun deserialize(decoder: Decoder) = error("Not implemented")

			override fun serialize(encoder: Encoder, value: ItemDisplay) {
				encoder.encodeStructure(descriptor) {
					value.item?.let { encodeSerializableElement(descriptor, 0, ItemStack.serializer(), it) }
					value.displayMode?.let { encodeSerializableElement(descriptor, 1, ItemDisplayModelMode.serializer(), it) }
					encodeDisplayEntity(value, descriptor, 2)
				}
			}
		}
	}
}

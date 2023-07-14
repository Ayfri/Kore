package features.itemmodifiers

import DataPack
import Generator
import arguments.Argument
import features.itemmodifiers.functions.ItemFunction
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

typealias ItemModifierAsList = @Serializable(with = ItemModifier.Companion.ItemModifierAsListSerializer::class) ItemModifier

@Serializable(with = ItemModifier.Companion.ItemModifierSerializer::class)
data class ItemModifier(
	@Transient
	override var fileName: String = "item_modifier",
	var modifiers: List<ItemFunction> = emptyList(),
) : Generator {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)

	companion object {
		object ItemModifierSerializer : KSerializer<ItemModifier> {
			override val descriptor = buildClassSerialDescriptor("ItemModifier")

			override fun deserialize(decoder: Decoder) = error("ItemModifier cannot be deserialized")

			override fun serialize(encoder: Encoder, value: ItemModifier) {
				val serializer = ItemFunction.Companion.ItemFunctionSerializer
				when (value.modifiers.size) {
					1 -> encoder.encodeSerializableValue(serializer, value.modifiers[0])
					else -> encoder.encodeSerializableValue(ListSerializer(serializer), value.modifiers)
				}
			}
		}

		object ItemModifierAsListSerializer : KSerializer<ItemModifier> by ItemModifierSerializer {
			override val descriptor = buildClassSerialDescriptor("ItemModifierAsList")
			override fun serialize(encoder: Encoder, value: ItemModifier) {
				encoder.encodeSerializableValue(ListSerializer(ItemFunction.Companion.ItemFunctionSerializer), value.modifiers)
			}
		}
	}
}

fun DataPack.itemModifier(fileName: String = "item_modifier", configure: ItemModifier.() -> Unit = {}): Argument.ItemModifier {
	itemModifiers += ItemModifier(fileName).apply(configure)
	return Argument.ItemModifier(fileName, name)
}

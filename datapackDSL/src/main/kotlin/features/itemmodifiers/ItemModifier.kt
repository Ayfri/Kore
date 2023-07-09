package features.itemmodifiers

import DataPack
import Generator
import arguments.Argument
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.File

@Serializable(with = ItemModifier.Companion.ItemModifierSerializer::class)
data class ItemModifier(
	@Transient var fileName: String = "item_modifier",
	var modifiers: List<ItemModifierEntry> = emptyList(),
) : Generator {
	override fun generate(dataPack: DataPack, directory: File) {
		File(directory, "$fileName.json").writeText(dataPack.jsonEncoder.encodeToString(this))
	}

	companion object {
		object ItemModifierSerializer : KSerializer<ItemModifier> {
			override val descriptor = buildClassSerialDescriptor("ItemModifier")

			override fun deserialize(decoder: Decoder) = error("ItemModifier cannot be deserialized")

			override fun serialize(encoder: Encoder, value: ItemModifier) {
				val serializer = ItemModifierEntry.Companion.ItemModifierEntrySerializer
				when (value.modifiers.size) {
					1 -> encoder.encodeSerializableValue(serializer, value.modifiers[0])
					else -> encoder.encodeSerializableValue(ListSerializer(serializer), value.modifiers)
				}
			}
		}
	}
}

fun DataPack.itemModifier(fileName: String = "item_modifier", configure: ItemModifierEntry.() -> Unit = {}): Argument.ItemModifier {
	itemModifiers += ItemModifier(fileName).apply {
		modifiers += ItemModifierEntry().apply(configure)
	}

	return Argument.ItemModifier(fileName, name)
}

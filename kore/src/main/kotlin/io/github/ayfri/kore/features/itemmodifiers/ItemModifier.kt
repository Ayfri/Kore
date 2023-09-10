package io.github.ayfri.kore.features.itemmodifiers

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.ItemModifierArgument
import io.github.ayfri.kore.features.itemmodifiers.functions.ItemFunction
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

typealias ItemModifierAsList = @Serializable(with = ItemModifier.Companion.ItemModifierAsListSerializer::class) ItemModifier

@Serializable
data class ItemModifier(
	@Transient
	override var fileName: String = "item_modifier",
	var modifiers: InlinableList<ItemFunction> = emptyList(),
) : Generator("item_modifier") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(modifiers)

	companion object {
		object ItemModifierAsListSerializer : KSerializer<ItemModifier> {
			override val descriptor = buildClassSerialDescriptor("ItemModifierAsList")

			override fun deserialize(decoder: Decoder) = error("ItemModifierAsList cannot be deserialized")

			override fun serialize(encoder: Encoder, value: ItemModifier) {
				encoder.encodeSerializableValue(serializer<List<ItemFunction>>(), value.modifiers)
			}
		}
	}
}

fun DataPack.itemModifier(fileName: String = "item_modifier", configure: ItemModifier.() -> Unit = {}): ItemModifierArgument {
	itemModifiers += ItemModifier(fileName).apply(configure)
	return ItemModifierArgument(fileName, name)
}

package io.github.ayfri.kore.features.itemmodifiers

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.itemmodifiers.functions.ItemFunction
import io.github.ayfri.kore.generated.arguments.types.ItemModifierArgument
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.inlinableListSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer

typealias ItemModifierAsList = @Serializable(with = ItemModifier.Companion.ItemModifierAsListSerializer::class) ItemModifier

@Serializable
data class ItemModifier(
	@Transient
	override var fileName: String = "item_modifier",
	var modifiers: InlinableList<ItemFunction> = emptyList(),
) : Generator("item_modifier") {
	override fun generateJson(dataPack: DataPack) =
		dataPack.jsonEncoder.encodeToString(inlinableListSerializer(ItemFunction.serializer()), modifiers)

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

fun DataPack.itemModifier(fileName: String = "item_modifier", init: ItemModifier.() -> Unit = {}): ItemModifierArgument {
	val itemModifier = ItemModifier(fileName).apply(init)
	itemModifiers += itemModifier
	return ItemModifierArgument(fileName, itemModifier.namespace ?: name)
}

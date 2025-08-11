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

/**
 * Inline serializer alias used when an item-modifier must be embedded as a raw JSON array
 * (e.g. inside the `sequence` or `filtered` function payloads). It serializes only the
 * underlying list of item functions.
 *
 * See docs: https://kore.ayfri.com/docs/item-modifiers
 */
typealias ItemModifierAsList = @Serializable(with = ItemModifier.Companion.ItemModifierAsListSerializer::class) ItemModifier

/**
 * Container for a list of item functions (vanilla "loot functions").
 *
 * The instance serialises to a JSON array matching the vanilla item-modifier format. Each
 * entry is a distinct function (e.g. `set_components`, `set_lore`, `exploration_map`, …).
 *
 * High-level overview and examples: https://kore.ayfri.com/docs/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class ItemModifier(
	@Transient
	override var fileName: String = "item_modifier",
	var modifiers: InlinableList<ItemFunction> = emptyList(),
) : Generator("item_modifier") {
	override fun generateJson(dataPack: DataPack) =
		dataPack.jsonEncoder.encodeToString(inlinableListSerializer(ItemFunction.serializer()), modifiers)

    companion object {
		data object ItemModifierAsListSerializer : KSerializer<ItemModifier> {
			override val descriptor = buildClassSerialDescriptor("ItemModifierAsList")

			override fun deserialize(decoder: Decoder) = error("ItemModifierAsList cannot be deserialized")

            override fun serialize(encoder: Encoder, value: ItemModifier) {
				encoder.encodeSerializableValue(serializer<List<ItemFunction>>(), value.modifiers)
			}
		}
	}
}

/**
 * Declare and register an item modifier file in the datapack.
 *
 * The created file will be saved as `data/<namespace>/item_modifier/<fileName>.json` and can be
 * referenced by `/item modify` or from loot tables.
 *
 * @param fileName target file name without extension
 * @param init builder to populate the list of item functions
 * @return an [ItemModifierArgument] you can pass to commands or other features
 *
 * See docs and examples: https://kore.ayfri.com/docs/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
fun DataPack.itemModifier(fileName: String = "item_modifier", init: ItemModifier.() -> Unit = {}): ItemModifierArgument {
	val itemModifier = ItemModifier(fileName).apply(init)
	itemModifiers += itemModifier
	return ItemModifierArgument(fileName, itemModifier.namespace ?: name)
}

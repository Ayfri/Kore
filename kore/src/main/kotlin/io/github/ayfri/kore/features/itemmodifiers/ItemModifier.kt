package io.github.ayfri.kore.features.itemmodifiers

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.itemmodifiers.functions.ItemFunction
import io.github.ayfri.kore.generated.arguments.types.ItemModifierArgument
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.InlineSerializer
import io.github.ayfri.kore.serializers.inlinableListSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.ListSerializer

/**
 * Inline serializer alias used when an item-modifier must be embedded as a raw JSON array
 * (e.g. inside the `sequence` or `filtered` function payloads). It serializes only the
 * underlying list of item functions.
 *
 * See docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 */
typealias ItemModifierAsList = @Serializable(with = ItemModifier.Companion.ItemModifierAsListSerializer::class) ItemModifier

/**
 * Data-driven container for loot/item functions applied to items.
 *
 * Item modifiers are ordered lists of functions used by loot tables and commands to transform
 * items (set components, set lore, enchant, map exploration data, etc.). Kore serializes this
 * as the vanilla JSON array format expected by Minecraft.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * Minecraft Wiki: https://minecraft.wiki/w/Item_modifier
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
	    data object ItemModifierAsListSerializer : InlineSerializer<ItemModifier, InlinableList<ItemFunction>>(
		    ListSerializer(ItemFunction.serializer()),
		    ItemModifier::modifiers
	    )
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
 * See docs and examples: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
fun DataPack.itemModifier(fileName: String = "item_modifier", init: ItemModifier.() -> Unit = {}): ItemModifierArgument {
	val itemModifier = ItemModifier(fileName).apply(init)
	itemModifiers += itemModifier
	return ItemModifierArgument(fileName, itemModifier.namespace ?: name)
}

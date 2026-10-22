package io.github.ayfri.kore.features.itemmodifiers

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.annotations.LootDsl
import io.github.ayfri.kore.features.itemmodifiers.functions.ItemFunction
import io.github.ayfri.kore.features.itemmodifiers.functions.ItemModifierReference
import io.github.ayfri.kore.features.itemmodifiers.functions.Sequence
import io.github.ayfri.kore.features.predicates.providers.NumberProviderScope
import io.github.ayfri.kore.generated.arguments.types.ItemModifierArgument
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.InlinableListSerializer
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * An [ItemModifier] embedded as a field (e.g. inside a `sequence` function or a loot table): a single function is
 * written bare, several ones as a list (the vanilla `sequence` shorthand).
 *
 * ```
 * [setCount]                -> { "type": "minecraft:set_count", ... }
 * [setCount, explosionDecay] -> [ { "type": "minecraft:set_count", ... }, { "type": "minecraft:explosion_decay" } ]
 * ```
 *
 * See docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 */
typealias InlinableItemModifier = @Serializable(with = ItemModifier.Companion.InlinableItemModifierSerializer::class) ItemModifier

/**
 * Data-driven container for loot/item functions applied to items.
 *
 * Item modifiers are ordered lists of functions used by loot tables and commands to transform
 * items (set components, set lore, enchant, map exploration data, etc.). An item modifier file holds exactly one
 * function object: a single function that isn't a reference is written as is, while several functions or a lone
 * [reference][io.github.ayfri.kore.features.itemmodifiers.functions.reference] are wrapped in a `minecraft:sequence`.
 * A file without any function isn't possible, generating it throws.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * Minecraft Wiki: https://minecraft.wiki/w/Item_modifier
 */
@LootDsl
@Serializable
data class ItemModifier(
	@Transient
	override var fileName: String = "item_modifier",
	var modifiers: InlinableList<ItemFunction> = emptyList(),
) : Generator("item_modifier"), NumberProviderScope {
	override fun generateJson(dataPack: DataPack): String {
		require(modifiers.isNotEmpty()) { "Item modifier file '$fileName' needs at least one function." }
		val function = modifiers.singleOrNull()?.takeUnless { it is ItemModifierReference } ?: Sequence(functions = this)
		return dataPack.jsonEncoder.encodeToString(ItemFunction.serializer(), function)
	}

	companion object {
		data object InlinableItemModifierSerializer : InlineAutoSerializer<ItemModifier, InlinableList<ItemFunction>>(
			InlinableListSerializer(ItemFunction.serializer()),
			ItemModifier::modifiers,
			{ ItemModifier(modifiers = it) },
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

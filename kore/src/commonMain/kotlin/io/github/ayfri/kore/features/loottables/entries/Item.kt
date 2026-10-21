package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.features.itemmodifiers.InlinableItemModifier
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import kotlinx.serialization.Serializable

/**
 * Loot entry that yields a concrete item.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable
data class Item(
	var name: ItemArgument,
	override var condition: PredicateCondition? = null,
	override var modifier: InlinableItemModifier? = null,
	var weight: Int? = null,
	var quality: Int? = null,
) : LootEntry()

/** Add an Item entry and configure it. */
fun LootEntries.item(name: ItemArgument, block: Item.() -> Unit = {}) {
	add(Item(name).apply(block))
}

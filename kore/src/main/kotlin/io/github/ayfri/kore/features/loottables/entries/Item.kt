package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifierAsList
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

/**
 * Loot entry that yields a concrete item.
 *
 * Docs: https://kore.ayfri.com/docs/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable
data class Item(
	var name: ItemArgument,
	var conditions: PredicateAsList? = null,
	var functions: ItemModifierAsList? = null,
	var weight: Int? = null,
	var quality: Int? = null,
) : LootEntry()

/** Add an Item entry and configure it. */
fun LootEntries.item(name: ItemArgument, block: Item.() -> Unit = {}) {
	add(Item(name).apply(block))
}

/** Set conditions, see [Predicates](https://kore.ayfri.com/docs/predicates). */
fun Item.conditions(block: Predicate.() -> Unit) {
	conditions = Predicate().apply(block)
}

/** Set item modifier functions, see [ItemModifiers](https://kore.ayfri.com/docs/item-modifiers). */
fun Item.functions(block: ItemModifier.() -> Unit) {
	functions = ItemModifier().apply(block)
}

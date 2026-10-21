package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.features.itemmodifiers.InlinableItemModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.conditions.PredicateReference
import io.github.ayfri.kore.features.predicates.toCondition
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
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
	var condition: PredicateCondition? = null,
	var modifier: InlinableItemModifier? = null,
	var weight: Int? = null,
	var quality: Int? = null,
) : LootEntry()

/** Add an Item entry and configure it. */
fun LootEntries.item(name: ItemArgument, block: Item.() -> Unit = {}) {
	add(Item(name).apply(block))
}

/** Set the condition, several conditions being wrapped in an `all_of`, see [Predicates](https://kore.ayfri.com/docs/data-driven/predicates). */
fun Item.condition(block: Predicate.() -> Unit) {
	condition = Predicate().apply(block).toCondition()
}

/** Set the condition to the predicate [predicate], written as its bare ID. */
fun Item.condition(predicate: PredicateArgument) {
	condition = PredicateReference(predicate)
}

/** Set the item modifier, see [ItemModifiers](https://kore.ayfri.com/docs/data-driven/item-modifiers). */
fun Item.modifier(block: ItemModifier.() -> Unit) {
	modifier = ItemModifier().apply(block)
}

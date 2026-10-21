package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.features.itemmodifiers.InlinableItemModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.toCondition
import kotlinx.serialization.Serializable

/**
 * Loot entry that yields nothing; useful for weight balancing.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable
data class Empty(
	var condition: PredicateCondition? = null,
	var modifier: InlinableItemModifier? = null,
	var quality: Int? = null,
	var weight: Int? = null,
) : LootEntry()

/** Add an Empty entry with given quality and weight, and configure it. */
fun LootEntries.empty(quality: Int = 1, weight: Int = 1, block: Empty.() -> Unit = {}) {
	add(Empty(quality = quality, weight = weight).apply(block))
}

/** Set the condition, several conditions being wrapped in an `all_of`, see [Predicates](https://kore.ayfri.com/docs/data-driven/predicates). */
fun Empty.condition(block: Predicate.() -> Unit) {
	condition = Predicate().apply(block).toCondition()
}

/** Set the item modifier, see [ItemModifiers](https://kore.ayfri.com/docs/data-driven/item-modifiers). */
fun Empty.modifier(block: ItemModifier.() -> Unit) {
	modifier = ItemModifier().apply(block)
}

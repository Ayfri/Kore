package io.github.ayfri.kore.features.loottables

import io.github.ayfri.kore.annotations.LootDsl
import io.github.ayfri.kore.features.itemmodifiers.InlinableItemModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.loottables.entries.LootEntries
import io.github.ayfri.kore.features.loottables.entries.LootEntry
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.NumberProviderScope
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.features.predicates.toCondition
import kotlinx.serialization.Serializable

/**
 * A pool of rolls that yields entries, optionally guarded by a condition and modified by an item modifier.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@LootDsl
@Serializable
data class LootPool(
	var bonusRolls: NumberProvider? = null,
	var condition: PredicateCondition? = null,
	var entries: List<LootEntry> = emptyList(),
	var modifier: InlinableItemModifier? = null,
	var rolls: NumberProvider,
) : NumberProviderScope

/** Set the number of rolls. */
fun LootPool.rolls(value: NumberProvider) {
	rolls = value
}

/** Set the number of rolls as a constant. */
fun LootPool.rolls(value: Float) = rolls(constant(value))

/** Set the number of bonus rolls. */
fun LootPool.bonusRolls(value: NumberProvider) {
	bonusRolls = value
}

/** Set the number of bonus rolls as a constant. */
fun LootPool.bonusRolls(value: Float) = bonusRolls(constant(value))

/** Set the predicate condition, several conditions being wrapped in an `all_of`. */
fun LootPool.condition(vararg value: PredicateCondition) {
	condition = Predicate(predicateConditions = value.toList()).toCondition()
}

/** Define the predicate condition via a builder, several conditions being wrapped in an `all_of`, see [Predicates](https://kore.ayfri.com/docs/data-driven/predicates). */
fun LootPool.condition(block: Predicate.() -> Unit) {
	condition = Predicate().apply(block).toCondition()
}

/** Replace entries with the provided list. */
fun LootPool.entries(vararg value: LootEntry) {
	entries = value.toList()
}

/** Build the entries list via a builder. */
fun LootPool.entries(block: LootEntries.() -> Unit) {
	entries = buildList(block)
}

/** Set the item modifier applied to the pool outputs, see [ItemModifiers](https://kore.ayfri.com/docs/data-driven/item-modifiers). */
fun LootPool.modifier(block: ItemModifier.() -> Unit) {
	modifier = ItemModifier().apply(block)
}

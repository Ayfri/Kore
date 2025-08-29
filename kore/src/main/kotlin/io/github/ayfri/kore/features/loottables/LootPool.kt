package io.github.ayfri.kore.features.loottables

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifierAsList
import io.github.ayfri.kore.features.loottables.entries.LootEntries
import io.github.ayfri.kore.features.loottables.entries.LootEntry
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.constant
import kotlinx.serialization.Serializable

/**
 * A pool of rolls that yields entries, optionally guarded by conditions and modified by functions.
 *
 * Docs: https://kore.ayfri.com/docs/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable
data class LootPool(
	var rolls: NumberProvider,
	var bonusRolls: NumberProvider? = null,
	var conditions: PredicateAsList? = null,
	var entries: List<LootEntry> = emptyList(),
	var functions: ItemModifierAsList? = null,
)

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

/** Add predicate conditions. */
fun LootPool.conditions(vararg value: PredicateCondition) {
	conditions = conditions?.also {
		it.predicateConditions += value.toList()
	} ?: Predicate(predicateConditions = value.toList())
}

/** Define predicate conditions via a builder, see [Predicates](https://kore.ayfri.com/docs/predicates). */
fun LootPool.conditions(block: Predicate.() -> Unit) {
	conditions = Predicate().apply(block)
}

/** Replace entries with the provided list. */
fun LootPool.entries(vararg value: LootEntry) {
	entries = value.toList()
}

/** Build the entries list via a builder. */
fun LootPool.entries(block: LootEntries.() -> Unit) {
	entries = buildList(block)
}

/** Set item modifier functions applied to the pool outputs, see [ItemModifiers](https://kore.ayfri.com/docs/item-modifiers). */
fun LootPool.functions(block: ItemModifier.() -> Unit) {
	functions = ItemModifierAsList().apply(block)
}

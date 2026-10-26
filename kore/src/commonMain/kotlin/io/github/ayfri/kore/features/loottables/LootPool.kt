package io.github.ayfri.kore.features.loottables

import io.github.ayfri.kore.annotations.LootDsl
import io.github.ayfri.kore.features.itemmodifiers.InlinableItemModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.loottables.entries.LootEntries
import io.github.ayfri.kore.features.loottables.entries.LootEntry
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.conditions.PredicateReference
import io.github.ayfri.kore.features.predicates.providers.ContextFloatProvider
import io.github.ayfri.kore.features.predicates.providers.ContextIntProvider
import io.github.ayfri.kore.features.predicates.providers.NumberProviderScope
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.features.predicates.providers.utils.toUniformProvider
import io.github.ayfri.kore.features.predicates.toCondition
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
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
	var bonusRolls: ContextFloatProvider? = null,
	var condition: PredicateCondition? = null,
	var entries: List<LootEntry> = emptyList(),
	var modifier: InlinableItemModifier? = null,
	var rolls: ContextIntProvider,
) : NumberProviderScope

/** Set the number of rolls. */
fun LootPool.rolls(value: ContextIntProvider) {
	rolls = value
}

/** Set the number of rolls as a constant. */
fun LootPool.rolls(value: Int) = rolls(constant(value))

/**
 * Set the number of rolls, drawn uniformly from [range], both ends included.
 *
 * ```
 * rolls(1..3)  // "rolls": {"type": "minecraft:uniform", "max": 3, "min": 1}
 * ```
 */
fun LootPool.rolls(range: IntRange) = rolls(range.toUniformProvider())

/** Set the number of bonus rolls. */
fun LootPool.bonusRolls(value: ContextFloatProvider) {
	bonusRolls = value
}

/** Set the number of bonus rolls as a constant. */
fun LootPool.bonusRolls(value: Float) = bonusRolls(constant(value))

/**
 * Set the number of bonus rolls, drawn uniformly from [range], the game never returning the end itself.
 *
 * ```
 * bonusRolls(0f..1f)  // "bonus_rolls": {"type": "minecraft:uniform", "max": 1.0, "min": 0.0}
 * ```
 */
fun LootPool.bonusRolls(range: ClosedFloatingPointRange<Float>) = bonusRolls(range.toUniformProvider())

/** Set the predicate condition, several conditions being wrapped in an `all_of`. */
fun LootPool.condition(vararg value: PredicateCondition) {
	condition = Predicate(predicateConditions = value.toList()).toCondition()
}

/** Define the predicate condition via a builder, several conditions being wrapped in an `all_of`, see [Predicates](https://kore.ayfri.com/docs/data-driven/predicates). */
fun LootPool.condition(block: Predicate.() -> Unit) {
	condition = Predicate().apply(block).toCondition()
}

/** Set the condition to the predicate [predicate], written as its bare ID. */
fun LootPool.condition(predicate: PredicateArgument) {
	condition = PredicateReference(predicate)
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

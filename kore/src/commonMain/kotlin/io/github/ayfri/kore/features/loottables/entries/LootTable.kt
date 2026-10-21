package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.features.itemmodifiers.InlinableItemModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.toCondition
import io.github.ayfri.kore.generated.arguments.types.LootTableArgument
import kotlinx.serialization.Serializable

/**
 * Loot entry that includes another loot table.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable
data class LootTable(
	var value: LootTableArgument,
	var condition: PredicateCondition? = null,
	var modifier: InlinableItemModifier? = null,
	var quality: Int? = null,
	var weight: Int? = null,
) : LootEntry()

/** Add and configure a nested loot table entry. */
fun LootEntries.lootTable(value: LootTableArgument, block: LootTable.() -> Unit = {}) {
	add(LootTable(value).apply(block))
}

/** Set the condition, several conditions being wrapped in an `all_of`, see [Predicates](https://kore.ayfri.com/docs/data-driven/predicates). */
fun LootTable.condition(block: Predicate.() -> Unit) {
	condition = Predicate().apply(block).toCondition()
}

/** Set the item modifier, see [ItemModifiers](https://kore.ayfri.com/docs/data-driven/item-modifiers). */
fun LootTable.modifier(block: ItemModifier.() -> Unit) {
	modifier = ItemModifier().apply(block)
}

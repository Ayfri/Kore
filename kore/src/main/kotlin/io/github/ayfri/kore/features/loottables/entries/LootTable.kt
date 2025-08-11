package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifierAsList
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.generated.arguments.types.LootTableArgument
import kotlinx.serialization.Serializable

/**
 * Loot entry that includes another loot table.
 *
 * Docs: https://kore.ayfri.com/docs/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable
data class LootTable(
	var name: LootTableArgument,
	var conditions: PredicateAsList? = null,
	var functions: ItemModifierAsList? = null,
	var quality: Int? = null,
	var weight: Int? = null,
) : LootEntry()

/** Add and configure a nested loot table entry. */
fun LootEntries.lootTable(name: LootTableArgument, block: LootTable.() -> Unit = {}) {
	add(LootTable(name).apply(block))
}

/** Set conditions, see [Predicates](https://kore.ayfri.com/docs/predicates). */
fun LootTable.conditions(block: PredicateAsList.() -> Unit) {
	conditions = PredicateAsList().apply(block)
}

/** Set item modifier functions, see [ItemModifiers](https://kore.ayfri.com/docs/item-modifiers). */
fun LootTable.functions(block: ItemModifier.() -> Unit) {
	functions = ItemModifier().apply(block)
}

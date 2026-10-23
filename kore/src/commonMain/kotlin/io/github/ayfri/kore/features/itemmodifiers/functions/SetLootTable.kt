package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.generated.arguments.types.LootTableArgument
import kotlinx.serialization.Serializable

/**
 * Sets a container block-entity's loot table on the item, including an optional seed.
 * Mirrors `minecraft:set_loot_table`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetLootTable(
	override var condition: PredicateCondition? = null,
	var lootTableId: LootTableArgument,
	var seed: Int? = null,
) : ItemFunction()

/** Add a `set_loot_table` step. */
fun ItemModifier.setLootTable(
	lootTableId: LootTableArgument,
	seed: Int? = null,
	block: SetLootTable.() -> Unit = {},
) {
	modifiers += SetLootTable(lootTableId = lootTableId, seed = seed).apply(block)
}

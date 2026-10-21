package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.generated.arguments.types.BlockEntityTypeArgument
import io.github.ayfri.kore.generated.arguments.types.LootTableArgument
import kotlinx.serialization.Serializable

/**
 * Sets a container block-entity's loot table on the item, including an optional seed.
 * Mirrors `minecraft:set_loot_table`.
 *
 * Not loadable in 26.3-snapshot-4: its block entity [type] collides with the function's `type` key, so Kore writes only
 * `"type": "minecraft:set_loot_table"` and the game can't decode the function either.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetLootTable(
	override var condition: PredicateCondition? = null,
	var seed: Int? = null,
	var tag: LootTableArgument,
	var type: BlockEntityTypeArgument,
) : ItemFunction()

/** Add a `set_loot_table` step. */
fun ItemModifier.setLootTable(
	type: BlockEntityTypeArgument,
	tag: LootTableArgument,
	seed: Int? = null,
	block: SetLootTable.() -> Unit = {},
) {
	modifiers += SetLootTable(seed = seed, tag = tag, type = type).apply(block)
}

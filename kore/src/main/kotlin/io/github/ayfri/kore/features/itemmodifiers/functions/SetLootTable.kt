package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.generated.arguments.types.BlockEntityTypeArgument
import io.github.ayfri.kore.generated.arguments.types.LootTableArgument
import kotlinx.serialization.Serializable

/**
 * Sets a container block-entity's loot table on the item, including an optional seed.
 * Mirrors `minecraft:set_loot_table`.
 *
 * Docs: https://kore.ayfri.com/docs/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetLootTable(
	override var conditions: PredicateAsList? = null,
	var type: BlockEntityTypeArgument,
	var name: String,
	var seed: Int? = null,
) : ItemFunction()

/** Add a `set_loot_table` step (name as a string). */
fun ItemModifier.setLootTable(type: BlockEntityTypeArgument, name: String, seed: Int? = null, block: SetLootTable.() -> Unit = {}) {
	modifiers += SetLootTable(type = type, name = name, seed = seed).apply(block)
}

/** Add a `set_loot_table` step (name via [LootTableArgument]). */
fun ItemModifier.setLootTable(
	type: BlockEntityTypeArgument,
	name: LootTableArgument,
	seed: Int? = null,
	block: SetLootTable.() -> Unit = {},
) {
	modifiers += SetLootTable(type = type, name = name.asString(), seed = seed)
		.apply(block)
}
